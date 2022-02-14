FROM maven:3.3-jdk-8

RUN apt-get update && apt-get install -y libxml2-utils && rm -rf /var/lib/apt/lists/*

ADD timbuctoo-prod-db /root/timbuctoo-prod-db
COPY cmd.sh /root/cmd.sh

ENV local_ref=master
ENV maventarget=test

CMD /root/cmd.sh