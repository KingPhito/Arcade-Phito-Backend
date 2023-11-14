FROM gradle:8-jdk17

ENV GRPC_JAVA_PROTOC_GEN_URL="https://repo1.maven.org/maven2/io/grpc/protoc-gen-grpc-java/1.57.2/protoc-gen-grpc-java-1.57.2-linux-x86_64.exe"

WORKDIR /workspace/.git/arcade-phito-backend

RUN git clone https://github.com/rdugue/Arcade-Phito-Backend.git .
RUN export PATH="/usr/local/gradle/bin:$PATH"
RUN export GRADLE_USER_HOME=`pwd`/.gradle
RUN wget $GRPC_JAVA_PROTOC_GEN_URL -O /usr/local/bin/protoc-gen-grpc-java.exe
RUN chmod +x /usr/local/bin/protoc-gen-grpc-java.exe
RUN chmod +x gradlew
RUN ./gradlew shadowJar

ENTRYPOINT ["java", "-jar", "build/libs/arcade-phito-grpc-fat-all.jar"]