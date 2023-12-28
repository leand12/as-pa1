#!/bin/sh

mvn package
gnome-terminal --tab --title="Client" -- java -cp target/PA3_G19-1.0-SNAPSHOT.jar Client.PClient
gnome-terminal --tab --title="Monitor" -- java -cp target/PA3_G19-1.0-SNAPSHOT.jar Monitor.PMonitor
gnome-terminal --tab --title="LoadBalancer" -- java -cp target/PA3_G19-1.0-SNAPSHOT.jar LB.PLoadBalancer
gnome-terminal --tab --title="Server" -- java -cp target/PA3_G19-1.0-SNAPSHOT.jar Server.PServer

