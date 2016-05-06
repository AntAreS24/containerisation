# -*- mode: ruby -*-
# vi: set ft=ruby :

require 'open-uri'


# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"
Vagrant.require_version ">= 1.6.0"
KUBERNETES_VERSION = ENV['KUBERNETES_VERSION'] || '1.2.2'

CHANNEL = ENV['CHANNEL'] || 'alpha'
COREOS_VERSION = ENV['COREOS_VERSION'] || 'latest'
upstream = "http://#{CHANNEL}.release.core-os.net/amd64-usr/#{COREOS_VERSION}"
if COREOS_VERSION == "latest"
	upstream = "http://#{CHANNEL}.release.core-os.net/amd64-usr/current"
	url = "#{upstream}/version.txt"
	RETRIEVED_COREOS_VERSION = open(url).read().scan(/COREOS_VERSION=.*/)[0].gsub('COREOS_VERSION=', '')
#else
#	RETRIEVED_COREOS_VERSION = #{COREOS_VERSION}
end

MASTER_MEM = ENV['MASTER_MEM'] || 1024
MASTER_CPUS = ENV['MASTER_CPUS'] || 1

BASE_IP_ADDR = ENV['BASE_IP_ADDR'] || "172.17.8"
MASTER_UNIQUE_IP = 100
MASTER_IP="#{BASE_IP_ADDR}.#{MASTER_UNIQUE_IP}"
DNS_DOMAIN=ENV['DNS_DOMAIN'] || "cluster.local"
NO_NODES=2


Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
	# always use Vagrants' insecure key
	config.ssh.insert_key = false
	config.ssh.forward_agent = true
	config.vm.provider :virtualbox do |v|
		# On VirtualBox, we don't have guest additions or a functional vboxsf
		# in CoreOS, so tell Vagrant that so it can be smarter.
		v.check_guest_additions = false
		v.functional_vboxsf     = false
	end
	# Fix to solve DNS issue with private Docker registry
	config.vm.provider :virtualbox do |v, override|
		v.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
		v.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
	end
	config.vm.box = "coreos-#{CHANNEL}"
	config.vm.box_version = ">= #{RETRIEVED_COREOS_VERSION}"
	config.vm.box_url = "#{upstream}/coreos_production_vagrant.json"
	config.vm.provision :file, :source => "./provision/make-certs.sh", :destination => "/tmp/make-certs.sh"
	
	## First, we build the master:
	config.vm.define "master" do |c|
		c.vm.hostname = "master"
		c.vm.network "private_network", ip: "#{MASTER_IP}"
		c.vm.network :forwarded_port, guest:5000, host:5000
		c.vm.network :forwarded_port, guest:8080, host:8080
		c.vm.network :forwarded_port, guest:9090, host:9090
		ETCD_SEED_CLUSTER = "#{c.vm.hostname}=http://#{MASTER_IP}:2380"
		c.vm.provision :file, :source => "./provision/master.yaml", :destination => "/tmp/vagrantfile-user-data"
		c.vm.provision :file, :source => "./provision/namespace.yaml", :destination => "namespace.yaml"
		c.vm.provision :file, :source => "./provision/k8s-service-be.json", :destination => "k8s-service-be.json"
		c.vm.provision :file, :source => "./provision/k8s-rc-be.json", :destination => "k8s-rc-be.json"
		c.vm.provision :shell, :privileged => true,
			inline: <<-EOF
				sed -i"*" "/__PROXY_LINE__/d" /tmp/vagrantfile-user-data
				sed -i"*" "s,__DOCKER_OPTIONS__,,g" /tmp/vagrantfile-user-data
				sed -i"*" "s,__RELEASE__,v#{KUBERNETES_VERSION},g" /tmp/vagrantfile-user-data
				sed -i"*" "s,__CHANNEL__,v#{CHANNEL},g" /tmp/vagrantfile-user-data
				sed -i"*" "s,__NAME__,#{c.vm.hostname},g" /tmp/vagrantfile-user-data
				sed -i"*" "s,__CLOUDPROVIDER__,,g" /tmp/vagrantfile-user-data
				sed -i"*" "s|__MASTER_IP__|#{MASTER_IP}|g" /tmp/vagrantfile-user-data
				sed -i"*" "s|__DNS_DOMAIN__|#{DNS_DOMAIN}|g" /tmp/vagrantfile-user-data
				#sed -i"*" "s|__ETCD_SEED_CLUSTER__|#{ETCD_SEED_CLUSTER}|g" /tmp/vagrantfile-user-data
				mv /tmp/vagrantfile-user-data /var/lib/coreos-vagrant/
			EOF
	end
	
	(1..(NO_NODES.to_i)).each do |i|
		hostname = "node%02d" % (i)
		config.vm.define "#{hostname}" do |c|
			c.vm.hostname = "#{hostname}"
			c.vm.network "private_network", ip: "#{BASE_IP_ADDR}.#{MASTER_UNIQUE_IP+i}"
			ETCD_SEED_CLUSTER_MASTER = "master=http://#{MASTER_IP}:2380"
			config.vm.provider :virtualbox do |v, override|
				v.memory = 2048
				v.cpus = 1
			end
			c.vm.synced_folder "be/", "/home/core/be/", :nfs => true, :mount_options => ['nolock,vers=3,udp']
			c.vm.provision :file, :source => "./provision/node.yaml", :destination => "/tmp/vagrantfile-user-data"
			c.vm.provision :shell, :privileged => true,
				inline: <<-EOF
					sed -i"*" "/__PROXY_LINE__/d" /tmp/vagrantfile-user-data
					sed -i"*" "s,__DOCKER_OPTIONS__,,g" /tmp/vagrantfile-user-data
					sed -i"*" "s,__RELEASE__,v#{KUBERNETES_VERSION},g" /tmp/vagrantfile-user-data
					sed -i"*" "s,__CHANNEL__,v#{CHANNEL},g" /tmp/vagrantfile-user-data
					sed -i"*" "s,__NAME__,#{c.vm.hostname},g" /tmp/vagrantfile-user-data
					sed -i"*" "s,__CLOUDPROVIDER__,,g" /tmp/vagrantfile-user-data
					sed -i"*" "s|__MASTER_IP__|#{MASTER_IP}|g" /tmp/vagrantfile-user-data
					sed -i"*" "s|__DNS_DOMAIN__|#{DNS_DOMAIN}|g" /tmp/vagrantfile-user-data
					#sed -i"*" "s|__ETCD_SEED_CLUSTER__|#{ETCD_SEED_CLUSTER_MASTER}|g" /tmp/vagrantfile-user-data
					mv /tmp/vagrantfile-user-data /var/lib/coreos-vagrant/
				EOF
			# Run BE docker image from TIBCO repo
			#c.vm.provision "docker" do |docker|
			#	docker.run "sample",
			#		image: "dockerregistry.tibco.com:5000/businessevents",
			#		image: "ddr.tibco.com:5000/businessevents",
			#		image: "#{MASTER_IP}:5000/businessevents",
			#		args: "-p 8090:8090 -p 8010:8010 --env-file ex-be1.env"
			#end
		end
	end
	
	
	
end
