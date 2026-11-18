pipeline {
    agent any

    environment {
        ANDROID_BUILD_IMAGE = 'catlas-jenkins-agent:local'
    }

    triggers {
        cron('H 2 * * *')
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timeout(time: 90, unit: 'MINUTES')
        timestamps()
    }

    stages {
        stage('Static analysis') {
            steps {
                sh 'docker run --platform linux/amd64 --rm -v "$WORKSPACE:/workspace" -v gradle-cache:/root/.gradle -w /workspace "$ANDROID_BUILD_IMAGE" ./gradlew --no-daemon :androidApp:detekt :shared:detekt :core:detekt :feature:home-feed:detekt :feature:catalog:detekt :feature:favorites:detekt :feature:profile:detekt :feature:camera:detekt'
            }
        }

        stage('Unit tests') {
            steps {
                sh 'docker run --platform linux/amd64 --rm -v "$WORKSPACE:/workspace" -v gradle-cache:/root/.gradle -w /workspace "$ANDROID_BUILD_IMAGE" ./gradlew --no-daemon :core:jvmTest :feature:home-feed:jvmTest :feature:catalog:jvmTest :feature:favorites:jvmTest :feature:profile:jvmTest :feature:camera:jvmTest :shared:jvmTest'
            }
            post {
                always {
                    junit testResults: '**/build/test-results/**/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Debug APK') {
            steps {
                sh 'docker run --platform linux/amd64 --rm -v "$WORKSPACE:/workspace" -v gradle-cache:/root/.gradle -w /workspace "$ANDROID_BUILD_IMAGE" ./gradlew --no-daemon :androidApp:assembleDebug'
            }
        }

        stage('Release bundle') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                }
            }
            steps {
                sh 'docker run --platform linux/amd64 --rm -v "$WORKSPACE:/workspace" -v gradle-cache:/root/.gradle -w /workspace "$ANDROID_BUILD_IMAGE" ./gradlew --no-daemon :androidApp:bundleRelease'
            }
        }

        stage('Nightly build benchmark') {
            when {
                allOf {
                    branch 'main'
                    triggeredBy 'TimerTrigger'
                }
            }
            steps {
                sh 'docker run --platform linux/amd64 --rm -v "$WORKSPACE:/workspace" -v gradle-cache:/root/.gradle -w /workspace "$ANDROID_BUILD_IMAGE" gradle-profiler --benchmark --project-dir /workspace --gradle-user-home /root/.gradle --scenario-file ci/gradle-profiler.scenarios --output-dir build/gradle-profiler clean_android_debug'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'build/gradle-profiler/**', allowEmptyArchive: true
                }
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'androidApp/build/outputs/apk/debug/*.apk, androidApp/build/outputs/bundle/release/*.aab', allowEmptyArchive: true, fingerprint: true
        }
    }
}
