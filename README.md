# containerisation
Initial project to run TIBCO BE in a local K8S cluster.

!!!! WARNING !!!!
This project is only to be used for demo purpose. The content requires to have access to the TIBCO network.

# Pre-requisits
+ You need Vagrant installed on the host machine.
+ You need to be connected to the TIBCO network to download and run the docker BusinessEvents image.

# steps
+ `vagrant up`
+ `vagrant ssh master`
+ `kubectl get services`
```console
core@master ~ $ kubectl get svc
NAME              CLUSTER-IP       EXTERNAL-IP    PORT(S)    AGE
kubernetes        10.100.0.1       <none>         443/TCP    2d
tibcobe-service   10.100.164.110   172.17.8.100   7000/TCP   17h
```
+ `kubectl get rc`
```console
core@master ~ $ kubectl get rc
NAME                   DESIRED   CURRENT   AGE
tibcobe                2         2         19h
```
+ `kubectl get pods`
```console
core@master ~ $ kubectl get pods
NAME                         READY     STATUS    RESTARTS   AGE
tibcobe-m5xgj                1/1       Running   0          18h
tibcobe-yfymc                1/1       Running   0          18h
```
+ Finally do a `kubectl describe pod <pod name as listed with the command above>` This will give you the Pod IP.
```console
core@master ~ $ kubectl describe pod tibcobe-m5xgj
Name:           tibcobe-m5xgj
Namespace:      default
Node:           172.17.8.101/172.17.8.101
Start Time:     Wed, 11 May 2016 05:25:40 +0000
Labels:         app=tibcobe,tier=backend
Status:         Running
IP:             172.18.0.3
Controllers:    ReplicationController/tibcobe
Containers:
  k8be:
    Container ID:       docker://2d4274053e73744f2cd3cc41b861d753864e4d12fe38d8161431e657438dd25c
    Image:              ddr.tibco.com:5000/businessevents
    Image ID:           docker://sha256:48e6bd0642a752cc871eb7f2151f23da719c30654c53dbc8801d0c43b5fae9a2
    Port:               7000/TCP
    QoS Tier:
      memory:           BestEffort
      cpu:              BestEffort
    State:              Running
      Started:          Wed, 11 May 2016 05:25:42 +0000
    Ready:              True
    Restart Count:      0
    Environment Variables:
      BE_ENGINE_EAR_1:  /data/be/sample.ear
      BE_ENGINE_CDD_1:  /data/be/sample.cdd
      BE_ENGINE_UNIT_1: sample-inf
      BE_ENGINE_TRA_1:  /data/be/be-engine.tra
      BE_ENGINE_JMX_1:  9990
Conditions:
  Type          Status
  Ready         True
Volumes:
  beconfig:
    Type:       HostPath (bare host directory volume)
    Path:       /home/core/be
  default-token-v4sdg:
    Type:       Secret (a volume populated by a Secret)
    SecretName: default-token-v4sdg
No events.
```
In my case, IP is `172.18.0.3`.
Note that the last step might take a while as it has to download the businessevents image from docker. 
+ Now, you can check the logs with `kubectl logs <pod name as listed with the command above>`
```console
core@master ~ $ kubectl logs tibcobe-m5xgj
Starting BE Engine 1 ...
/opt/tibco/be/5.2/bin/be-engine --propFile /data/be/be-engine.tra -c /data/be/sample.cdd --propVar jmx_port=9990 -u sample-inf /data/be/sample.ear
Using property file: /data/be/be-engine.tra
2016 May 11 05:25:47:258 GMT 0 tibcobe-m5xgj Warning [main] - [runtime.service] Could not load payload class:  com.tibco.cep.driver.tibrv.serializer.TibrvMsgPayload
Loading modules from :jar:file:/opt/tibco/be/5.2/lib/cep-modules.jar!/cep-modules.xml
Ignoring module :file:/opt/tibco/be/5.2/lib/cep-modules.jar!/cep-modules.xml already loaded from a different location
2016 May 11 05:25:47:383 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]
****************************************************************************
        TIBCO BusinessEvents 5.2.2.077 (2015-11-09)
        Using arguments :-c /data/be/sample.cdd -u sample-inf /data/be/sample.ear
        Copyright(c) 2004-2015 TIBCO Software Inc. All rights reserved.

****************************************************************************

2016 May 11 05:25:47:384 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone] TIBCO BusinessEvents 5.2.2.077 -c /data/be/sample.cdd -u sample-inf /data/be/sample.ear
2016 May 11 05:25:47:439 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone] Versions currently running:
2016 May 11 05:25:47:441 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  be-functions.jar         : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:442 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  be-mm.jar                : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:442 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  be-q.jar                 : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:444 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  be-studiofunctions.jar   : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:444 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-analytics.jar        : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:444 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-as-channel.jar       : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:444 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-backingstore-bdb.jar : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:445 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-backingstore.jar     : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:448 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-base.jar             : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:453 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-bpmn.jar             : Version 1.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:453 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-common.jar           : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:453 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-container.jar        : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:454 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-datagrid-oracle.jar  : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:454 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-datagrid-tibco.jar   : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:454 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-drivers.jar          : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:455 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-ftl.jar              : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:455 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-hawk-channel.jar     : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:455 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-interpreter.jar      : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:456 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-kernel.jar           : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:456 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-loadbalancer.jar     : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:456 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-migration.jar        : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:456 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-modules.jar          : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:457 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-pattern.jar          : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:457 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-query.jar            : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:457 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-sb-channel.jar       : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:458 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-security.jar         : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:458 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone]  cep-ui-rt-common.jar     : Version 5.2.2.077, 2015-11-09 - TIBCO BusinessEvents
2016 May 11 05:25:47:643 GMT 0 tibcobe-m5xgj Info [main] - [container.standalone] Class path report:
    [file:/opt/tibco/be/5.2/lib/be-functions.jar]
    [file:/opt/tibco/be/5.2/mm/lib/be-mm.jar]
    [file:/opt/tibco/be/5.2/lib/cep-analytics.jar]
    [file:/opt/tibco/be/5.2/lib/cep-as-channel.jar]
    [file:/opt/tibco/be/5.2/lib/cep-base.jar]
    [file:/opt/tibco/be/5.2/lib/cep-bpmn.jar]
    [file:/opt/tibco/be/5.2/lib/cep-common.jar]
    [file:/opt/tibco/be/5.2/lib/cep-container.jar]
    [file:/opt/tibco/be/5.2/lib/cep-datagrid-oracle.jar]
    [file:/opt/tibco/be/5.2/lib/cep-datagrid-tibco.jar]
    [file:/opt/tibco/be/5.2/lib/cep-drivers.jar]
    [file:/opt/tibco/be/5.2/lib/cep-ftl.jar]
    [file:/opt/tibco/be/5.2/lib/cep-hawk-channel.jar]
    [file:/opt/tibco/be/5.2/lib/cep-interpreter.jar]
    [file:/opt/tibco/be/5.2/lib/cep-kernel.jar]
    [file:/opt/tibco/be/5.2/lib/cep-loadbalancer.jar]
    [file:/opt/tibco/be/5.2/lib/cep-migration.jar]
    [file:/opt/tibco/be/5.2/lib/cep-modules.jar]
    [file:/opt/tibco/be/5.2/lib/cep-pattern.jar]
    [file:/opt/tibco/be/5.2/mm/lib/be-q.jar]
    [file:/opt/tibco/be/5.2/lib/cep-sb-channel.jar]
    [file:/opt/tibco/be/5.2/lib/cep-security.jar]
2016 May 11 05:25:47:646 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session] Loading catalog functions.
2016 May 11 05:25:47:650 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session] Loading the project from: /data/be/sample.ear
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/opt/tibco/be/5.2/lib/ext/tpcl/slf4j-log4j12-1.7.7.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/opt/tibco/be/5.2/lib/ext/tpcl/tomsawyer/jena.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/opt/tibco/be/5.2/lib/ext/tibco/sbclient.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.slf4j.impl.Log4jLoggerFactory]
2016 May 11 05:26:04:219 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session] Loaded the project successfully
2016 May 11 05:26:46:440 GMT 0 tibcobe-m5xgj Info [Catalog-Functions-Loader] - [runtime.session] Loaded catalog functions.
2016 May 11 05:26:46:442 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session] Project XPATH version:2.0
2016 May 11 05:26:46:443 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session] Registering Ontology Classes...
2016 May 11 05:26:46:501 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session] Registered Ontology Classes
2016 May 11 05:26:47:148 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session] Loaded BE Jar catalog functions
2016 May 11 05:26:47:272 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session] Starting BE engine in PRIMARY mode
2016 May 11 05:26:47:273 GMT 0 tibcobe-m5xgj Info [main] - [runtime.channel] Initialized Channel Manager
2016 May 11 05:26:47:972 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session] EAR generated with:
2016 May 11 05:26:47:973 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  be-dashboardagent.jar                                   : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:973 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  be-functions.jar                                        : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:973 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  be-q.jar                                                : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:973 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  be-studiofunctions.jar                                  : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:973 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/be-functions.jar            : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:973 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/be-studiofunctions.jar      : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:973 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/cep-analytics.jar           : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:973 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/cep-as-channel.jar          : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:974 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/cep-backingstore.jar        : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:974 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/cep-base.jar                : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:974 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/cep-common.jar              : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:974 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/cep-container.jar           : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:974 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/cep-datagrid-oracle.jar     : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:976 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/cep-datagrid-tibco.jar      : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:976 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/cep-drivers.jar             : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:980 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/cep-ftl.jar                 : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:980 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/cep-hawk-channel.jar        : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:981 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/cep-interpreter.jar         : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:982 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/cep-kernel.jar              : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:982 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/cep-loadbalancer.jar        : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:982 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/cep-pattern.jar             : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:982 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/cep-query.jar               : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:984 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/cep-sb-channel.jar          : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:984 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/lib/cep-security.jar            : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:984 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/mm/lib/be-q.jar                 : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:984 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/views/lib/be-dashboardagent.jar : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:47:985 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session]  c:/tibco/be_home/be/5.2/views/lib/cep-metric.jar        : Version 5.2.2.077, 2015-11-09 -
2016 May 11 05:26:48:046 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session] [inference-class] Using ObjectManager type :com.tibco.cep.runtime.service.om.impl.mem.InMemoryObjectManager@74cbbd4 Concurrent : false
2016 May 11 05:26:48:093 GMT 0 tibcobe-m5xgj Info [main] - [runtime.scheduler] [inference-class] Initialized thread pool [$default.be.mt$] with max [10] threads and max [1024] job queue size.
2016 May 11 05:26:48:600 GMT 0 tibcobe-m5xgj Info [main] - [runtime.service] Registering all BE-Agent level Group MBeans for BE-Agent with ID: 0
2016 May 11 05:26:48:609 GMT 0 tibcobe-m5xgj Info [main] - [runtime.service] All BE-Agent level Group MBeans SUCCESSFULLY registered for BE-Agent with ID: 0
2016 May 11 05:26:48:635 GMT 0 tibcobe-m5xgj Info [main] - [runtime.session] Hot Deployment Request, Loading Classes From File System
2016 May 11 05:26:48:636 GMT 0 tibcobe-m5xgj Info [main] - [driver.http] HTTP Channel[Port:7000] [Thread]main [State]Initialized
2016 May 11 05:26:48:647 GMT 0 tibcobe-m5xgj Info [main] - [driver.http] Started Channel [/Channels/HTTPChannel]
2016 May 11 05:26:48:675 GMT 0 tibcobe-m5xgj Info [main] - [studio.core] Hot Deployment enabled, using Java Instrumentation: sun.instrument.InstrumentationImpl
2016 May 11 05:26:48:687 GMT 0 tibcobe-m5xgj Info [main] - [studio.core] Started Hot Deployer
2016 May 11 05:26:48:695 GMT 0 tibcobe-m5xgj Info [main] - [runtime.service] Registering all BE-Engine level Group MBeans...
2016 May 11 05:26:48:709 GMT 0 tibcobe-m5xgj Info [main] - [runtime.service] All BE-Engine level Group MBeans SUCCESSFULLY registered
2016 May 11 05:26:48:709 GMT 0 tibcobe-m5xgj None [main] - [runtime.session] BE Engine tibcobe-m5xgj started
May 11, 2016 5:26:49 AM org.apache.coyote.AbstractProtocol init
INFO: Initializing ProtocolHandler ["http-nio-7000"]
May 11, 2016 5:26:49 AM org.apache.tomcat.util.net.NioSelectorPool getSharedSelector
INFO: Using a shared selector for servlet write/read
May 11, 2016 5:26:49 AM org.apache.catalina.core.StandardService startInternal
INFO: Starting service Tomcat
May 11, 2016 5:26:49 AM org.apache.catalina.core.StandardEngine startInternal
INFO: Starting Servlet Engine: Apache Tomcat/7.0.64
May 11, 2016 5:26:49 AM org.apache.catalina.startup.TldConfig execute
INFO: At least one JAR was scanned for TLDs yet contained no TLDs. Enable debug logging for this logger for a complete list of JARs that were scanned but no TLDs were found in them. Skipping unneeded
JARs during scanning can improve startup time and JSP compilation time.
2016 May 11 05:26:49:684 GMT 0 tibcobe-m5xgj Info [HTTP-Channel-Startup] - [driver.http] Channel server for HTTP Channel[Port:7000] starting
May 11, 2016 5:26:49 AM org.apache.coyote.AbstractProtocol start
INFO: Starting ProtocolHandler ["http-nio-7000"]
2016 May 11 05:26:49:691 GMT 0 tibcobe-m5xgj Info [HTTP-Channel-Startup] - [driver.http] Channel server for HTTP Channel[Port:7000] successfully started
2016 May 11 05:38:34:809 GMT 0 tibcobe-m5xgj Info [$default.be.mt$.Thread.1] - [Rules.NormalProcessing] [inference-class] Starting the normal processing...
2016 May 11 05:38:34:809 GMT 0 tibcobe-m5xgj Info [$default.be.mt$.Thread.1] - [Rules.NormalProcessing] [inference-class] Normal processing completed.
```
+ Since the pod IP is only available on the Node, you will have to connect to the node to check the next commands.
```console
exit
vagrant ssh node01
```
+ Check if the Pod is working by doing `curl http://<ip returned for the pod>:7000/Channels/HTTPChannel/trigger_processing?name=normal&number=1`

Check again the logs.
