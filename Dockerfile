FROM gigaspaces/xap-enterprise:15.5.0-m13-tue-10
ENV GS_LICENSE tryme
ENV GS_OPTIONS_EXT "-Djava.rmi.server.hostname=127.0.1.1 -Dcom.gigaspaces.grid.gsc.serviceLimit=500"
ENV GS_LOOKUP_GROUPS xap-15.5.0
RUN cp /opt/gigaspaces/lib/optional/mongodb/xap-mongodb.jar /opt/gigaspaces/lib/optional/pu-common/
RUN cp /opt/gigaspaces/lib/optional/kafka/xap-kafka.jar /opt/gigaspaces/lib/optional/pu-common/
