package com.ladamengh.petproject

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ladamengh.petproject.app.CatlasApp
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class CatlasHomeFeedUiTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport(),
) {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setTestContent() {
        composeRule.setContent {
            CatlasApp(CatlasUiTestDependencies.isolatedKoinApplication)
        }
    }

    @Test
    fun userLoadsCatFromServerAndOpensItsDetails() = run {
        step("Wait for the mocked cat to load") {
            composeRule.waitUntil(5_000) {
                composeRule.onAllNodesWithText("Луна").fetchSemanticsNodes().isNotEmpty()
            }
            ComposeScreen.onComposeScreen<CatlasHomeScreen>(composeRule) {
                cat { assertIsDisplayed() }
            }
        }

        step("Open and verify the cat details") {
            ComposeScreen.onComposeScreen<CatlasHomeScreen>(composeRule) {
                cat { performClick() }
                detailsTitle { assertIsDisplayed() }
                detailsBack { assertIsDisplayed() }
                description { assertIsDisplayed() }
                homeTitle { assertDoesNotExist() }
            }
        }

        step("Return to the feed with Espresso back") {
            pressBack()
            ComposeScreen.onComposeScreen<CatlasHomeScreen>(composeRule) {
                homeTitle { assertIsDisplayed() }
                detailsTitle { assertDoesNotExist() }
            }
        }

        step("Verify the feed request") {
            val request = CatlasUiTestDependencies.mockWebServer.takeRequest(5, TimeUnit.SECONDS)
            assertNotNull("The app did not send a request to MockWebServer", request)
            val recordedRequest = requireNotNull(request)
            assertEquals("GET", recordedRequest.method)
            assertEquals("/rest/v1/cats", recordedRequest.requestUrl?.encodedPath)
            assertEquals(
                "id,name,breed,location,description,image_url",
                recordedRequest.requestUrl?.queryParameter("select"),
            )
            assertEquals("id.desc", recordedRequest.requestUrl?.queryParameter("order"))
        }
    }

    companion object {
        @ClassRule
        @JvmField
        val testDependencies = object : ExternalResource() {
            override fun before() {
                CatlasUiTestDependencies.start(
                    androidx.test.core.app.ApplicationProvider.getApplicationContext(),
                )
            }

            override fun after() {
                CatlasUiTestDependencies.stop()
            }
        }
    }
}

class CatlasHomeScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider,
) : ComposeScreen<CatlasHomeScreen>(semanticsProvider) {
    val cat = onNode { hasText("Луна") }
    val detailsTitle = onNode { hasText("О котике") }
    val detailsBack = onNode { hasContentDescription("Назад к ленте") }
    val description = onNode { hasText("Спокойная кошка ищет дом") }
    val homeTitle = onNode { hasText("Знакомься с котиками") }
}
