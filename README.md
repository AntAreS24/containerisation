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
+ Finally do a `kubectl describe pod <pod name as listed with the command above>` This will give you the Pod IP.

Note that the last step might take a while as it has to download the businessevents image from docker. 

Now, you can check the logs with `kubectl logs <pod name as listed with the command above>`

Check if the Pod is working by doing `curl http://<ip returned for the pod>:7000/Channels/HTTPChannel/trigger_processing?name=normal&number=1`

Check again the logs.
