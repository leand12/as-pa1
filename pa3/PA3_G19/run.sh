#!/bin/sh

javac -d target $(find . | grep "\.java$")

gnome-terminal --tab --title="Client" -- java -cp target Client.PClient
gnome-terminal --tab --title="Monitor" -- java -cp target Monitor.PMonitor
gnome-terminal --tab --title="LoadBalancer" -- java -cp target LB.PLoadBalancer
gnome-terminal --tab --title="Server" -- java -cp target Server.PServer
