# Installation Document OC Vietnam

## VPS (virtual server) requirements

- CPU: 4 cores
- Memory: 16GB
- Disk: 128GB SSD

## Software Prerequisites

- Operating system: Ubuntu Server x64 16.04 LTS or upper
- MongoDB 3.4 (will be installed below)

## Install dependencies

### Install MongoDB

Please refer to this comprehensive documentation page about how to install MongoDB on Ubuntu
https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/

quick list of commands that worked for us:

```
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 0C49F3730359A14518585931BC711F9BA15703C6
echo "deb [ arch=amd64,arm64 ] http://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/3.4 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.4.list
sudo apt update
sudo apt install mongodb-org
sudo systemctl enable mongod
sudo systemctl start mongod
```


## Install OpenJDK 8.0

`$ sudo apt install openjdk-8-jdk`

## Create user oce

`$ sudo useradd -m oce`

## Create the Derby db with users

### Create install dir

```
$ sudo mkdir /derby
$ sudo chown -R oce:oce /derby
```

### Unzip provided derby database backup

```
$ sudo apt install p7zip
$ wget http://url-to-derby-download-provided-by-dg-team (alternatively you can receive this file as an attachment from the ocvn development team)
$ 7zr x -o/derby ocvn-derby-*.7z
```

## Download and compile the open source code from github.com

### Install Maven

`$ sudo apt install maven`

### Get the source code


```
$ su - oce
$ git clone https://github.com/devgateway/ocvn.git
```

### Compile the code

```
$ cd ocvn
$ git checkout master
$ mvn -Dmaven.javadoc.skip=true -Dmaven.test.skip=true install
```

### Copy artifact and config to startup locatinon

```
$ cd ~
$ cp forms/target/forms-*-SNAPSHOT.jar ocvn.jar
$ cp forms/forms.conf ocvn.conf
```

### Edit configuration file ocvn.conf

- Replace {website.url} with your website's URL

### Make symlink to enable startup as service

```
$ sudo ln -s /home/oce/ocvn.jar /etc/init.d/ocvn
$ sudo update-rc.d ocvn defaults
```

## Start the server

`$ sudo service ocvn start`

## Setup autoimport

- Go to http://localhost:8090 and login using the admin account
- Go to the menu Admin->Settings and fill in `Admin Notification Email` with the email of the administrative person in charge with receiving import notifications and `Import Files Path` , with the path on the local machine where the most up to date import files are kept. This folder has to have the same owner as the process that runs the server, in our case `oce`
- Set to ON the `Enable Daily Automated Import ` 
- Click save and restart server using 

`$ sudo service ocvn restart`


Example: if `Import Files Path` is set to `/opt/ocvn/import` then this is the contents of the `/opt/ocvn/import` folder on the server machine:

```
$ cd /opt/ocvn/import/
$ ls -la
total 85796
drwxr-xr-x. 2 oce     oce     4096 Jun 26 06:21 .
drwxr-xr-x. 4 oce     oce     4096 Jul 12 21:00 ..
-rw-r--r--. 1 oce     oce   82876783 Jul  6 06:43 egp.xlsx
-rw-r--r--. 1 oce     oce    11929 May 17 08:36 Location_Table_Geocoded.xlsx
-rw-r--r--. 1 oce     oce    14231 May 17 08:36 OCVN_city_department_group.xlsx
-rwxr-xr-x. 1 oce     oce  4938870 Jul  6 06:42 UM_PUBINSTITU_SUPPLIERS.xlsx
```

- egp.xlsx is the Prototype Database File
- UM_PUBINSTITU_SUPPLIERS.xlsx is the Public Institutions and Suppliers File
- Location_Table_Geocoded.xlsx is the Locations File
- OCVN_city_department_group.xlsx is the Cities-Departments-Groups File

Usually the first two are generated on a daily basis by a third party script, from the original procurement database source. They have to be copied here and overriden. The other two files (locations + cities) are usually not changed from one import to the other.

With this, the internal scheduler will run the import process daily, read the 4 files, parse them and import the result into the mongodb database. If the import is successful and without errors the live database is automatically replaced with the import. If the import has critical errors then an email is sent to the admin using the localhost:25 SMTP server. Alternatively the admin can check the logs of the last day's import , available in `/var/log/ocvn.log` and see the log file with all the errors, none of them should be marked as `CRITICAL` if the import has succeeded. 



