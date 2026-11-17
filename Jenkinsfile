pipeline {
    agent {
        dockerfile {
            filename 'Dockerfile'
            args '-v gradle-cache:/root/.gradle'
        }
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
                sh './gradlew --no-daemon :androidApp:detekt :shared:detekt :core:detekt :feature:home-feed:detekt :feature:catalog:detekt :feature:favorites:detekt :feature:profile:detekt :feature:camera:detekt'
            }
        }

        stage('Unit tests') {
            steps {
                sh './gradlew --no-daemon test'
            }
        }

        stage('Debug APK') {
            steps {
                sh './gradlew --no-daemon :androidApp:assembleDebug'
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
                sh './gradlew --no-daemon :androidApp:bundleRelease'
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
                sh 'gradle-profiler --benchmark --project-dir "$WORKSPACE" --gradle-user-home "$HOME/.gradle" --scenario-file ci/gradle-profiler.scenarios --output-dir build/gradle-profiler clean_android_debug'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'build/gradle-profiler/**', allowEmptyArchive: true
                }
            }
        }
    }

    post {
        always {
            junit testResults: '**/build/test-results/**/*.xml', allowEmptyResults: true
        }
        success {
            archiveArtifacts artifacts: 'androidApp/build/outputs/apk/debug/*.apk, androidApp/build/outputs/bundle/release/*.aab', allowEmptyArchive: true, fingerprint: true
        }
    }
}
