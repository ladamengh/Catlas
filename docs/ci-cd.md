# CI/CD

## GitHub Actions

The workflow in .github/workflows/android-ci.yml runs on every push and pull request. It runs Detekt, unit tests and the Android debug build. Pushes to main or master also build a release AAB.

Successful runs upload the APK and, when built, the AAB under Actions → workflow run → Artifacts. JUnit XML files are attached to the run for review.

## Jenkins

Configure Jenkins as a Multibranch Pipeline and point it to this repository. The Jenkins agent needs Docker access and the Docker Pipeline and JUnit plugins. Jenkins builds the root Dockerfile as its Android build agent.

Every branch runs Detekt, unit tests and the debug build. The main and master branches also build an AAB. Jenkins schedules a daily run with H 2 * * *; on main, timer-triggered builds also run the Gradle Profiler scenario in ci/gradle-profiler.scenarios and archive its results.

The release AAB is an artifact only. Publishing to Google Play requires separate Jenkins credentials and a publishing step.
