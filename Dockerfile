FROM eclipse-temurin:17-jdk-jammy

ARG ANDROID_CMDLINE_TOOLS_VERSION=11076708
ARG GRADLE_PROFILER_VERSION=0.26.0

ENV ANDROID_SDK_ROOT=/opt/android-sdk \
    ANDROID_HOME=/opt/android-sdk \
    PATH=/opt/android-sdk/cmdline-tools/latest/bin:/opt/android-sdk/platform-tools:$PATH

RUN apt-get update \
    && apt-get install --no-install-recommends -y ca-certificates curl git unzip \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p "${ANDROID_SDK_ROOT}/cmdline-tools" \
    && curl --fail --silent --show-error --location \
        "https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_CMDLINE_TOOLS_VERSION}_latest.zip" \
        --output /tmp/android-command-line-tools.zip \
    && unzip -q /tmp/android-command-line-tools.zip -d "${ANDROID_SDK_ROOT}/cmdline-tools" \
    && mv "${ANDROID_SDK_ROOT}/cmdline-tools/cmdline-tools" "${ANDROID_SDK_ROOT}/cmdline-tools/latest" \
    && rm /tmp/android-command-line-tools.zip \
    && yes | sdkmanager --sdk_root="${ANDROID_SDK_ROOT}" --licenses >/dev/null \
    && sdkmanager --sdk_root="${ANDROID_SDK_ROOT}" \
        "platform-tools" \
        "platforms;android-37"

RUN curl --fail --silent --show-error --location \
        "https://github.com/gradle/gradle-profiler/releases/download/v${GRADLE_PROFILER_VERSION}/gradle-profiler-${GRADLE_PROFILER_VERSION}.zip" \
        --output /tmp/gradle-profiler.zip \
    && unzip -q /tmp/gradle-profiler.zip -d /opt \
    && ln -s "/opt/gradle-profiler-${GRADLE_PROFILER_VERSION}/bin/gradle-profiler" /usr/local/bin/gradle-profiler \
    && rm /tmp/gradle-profiler.zip

WORKDIR /workspace
