package com.ladamengh.petproject

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import catlas.shared.generated.resources.Res
import catlas.shared.generated.resources.ic_cat
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private sealed interface AppTabIcon {
    data class Material(val imageVector: ImageVector) : AppTabIcon
    data class Resource(val drawableResource: DrawableResource) : AppTabIcon
}

private data class AppTab(
    val title: String,
    val icon: AppTabIcon,
)

private val appTabs = listOf(
    AppTab(title = "Домой", icon = AppTabIcon.Material(Icons.Filled.Home)),
    AppTab(title = "Каталог", icon = AppTabIcon.Material(Icons.Filled.GridView)),
    AppTab(title = "Камера", icon = AppTabIcon.Material(Icons.Filled.PhotoCamera)),
    AppTab(title = "Избранное", icon = AppTabIcon.Material(Icons.Filled.Favorite)),
    AppTab(title = "Профиль", icon = AppTabIcon.Resource(Res.drawable.ic_cat)),
)

@Composable
fun App() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val selectedTab = appTabs[selectedTabIndex]

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFFFFBFE),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                FeedContent(
                    selectedTab = selectedTab,
                    modifier = Modifier.weight(1f),
                )

                BottomTabBar(
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it },
                )
            }
        }
    }
}

@Composable
private fun FeedContent(
    selectedTab: AppTab,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppTabIconView(
            icon = selectedTab.icon,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = selectedTab.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (selectedTab.title == "Домой") {
                "Здесь появится лента фотографий котиков"
            } else {
                "Раздел пока готовится"
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun BottomTabBar(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        appTabs.forEachIndexed { index, tab ->
            val isSelected = index == selectedTabIndex
            val contentColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(index) }
                    .background(
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            Color.Transparent
                        },
                        shape = RoundedCornerShape(16.dp),
                    )
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AppTabIconView(
                    icon = tab.icon,
                    contentDescription = tab.title,
                    modifier = Modifier.size(26.dp),
                    tint = contentColor,
                )
            }
        }
    }
}

@Composable
private fun AppTabIconView(
    icon: AppTabIcon,
    contentDescription: String?,
    modifier: Modifier,
    tint: Color,
) {
    when (icon) {
        is AppTabIcon.Material -> Icon(
            imageVector = icon.imageVector,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint,
        )

        is AppTabIcon.Resource -> Icon(
            painter = painterResource(icon.drawableResource),
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint,
        )
    }
}
