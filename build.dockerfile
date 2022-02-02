### TODO currently not working

FROM python:3.10

RUN apt-get update
RUN apt-get install -y software-properties-common

# https://docs.aws.amazon.com/corretto/latest/corretto-8-ug/generic-linux-install.html
RUN wget -O- https://apt.corretto.aws/corretto.key | apt-key add -
RUN add-apt-repository 'deb https://apt.corretto.aws stable main'

#https://docs.microsoft.com/en-us/dotnet/core/install/linux-debian
RUN wget https://packages.microsoft.com/config/debian/11/packages-microsoft-prod.deb -O packages-microsoft-prod.deb
RUN dpkg -i packages-microsoft-prod.deb
RUN rm packages-microsoft-prod.deb
RUN apt-get install -y apt-transport-https

RUN apt-get update
RUN apt-get install -y dotnet-sdk-3.1 maven java-1.8.0-amazon-corretto-jdk java-11-amazon-corretto-jdk
#ca-certificates-java
RUN apt-get clean
#RUN update-ca-certificates -f

# https://github.com/nodesource/distributions/blob/master/README.md
RUN curl -fsSL https://deb.nodesource.com/setup_14.x | bash -
RUN apt-get install -y nodejs


ENV JAVA_HOME_8  /usr/lib/jvm/java-1.8.0-amazon-corretto
RUN export JAVA_HOME_8
ENV JAVA_HOME_11 /usr/lib/jvm/java-11-amazon-corretto
RUN export JAVA_HOME_11

WORKDIR /emb

COPY dotnet_3 dotnet_3
COPY jdk_8_maven jdk_8_maven
COPY jdk_11_maven jdk_11_maven
COPY jdk_11_gradle jdk_11_gradle
COPY js_npm js_npm
COPY scripts scripts

### Currently failing due to SNAPSHOT dependencies and link to client-js
#RUN python3 scripts/dist.py

CMD ["bash"]


# export DOCKER_BUILDKIT=0
# docker build -f build.dockerfile -t emb .
# docker run  --entrypoint bash  -it emb