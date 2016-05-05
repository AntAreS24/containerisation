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
+ (should return 2 services)
+ `kubectl get rc`
(should return 1 RC)
+ `kubectl get pods`
(should return 2 pods with "running" status)

Note that the last step might take a while as it has to download the businessevents image from docker. 