#!/bin/bash

SERVER=$1
PORT=$2
NUM_MSGS=$3
JAR_PATH="~/cs455/455Homework2/build/libs/Team5-HW2.jar"
USERNAME=mjmccann

# can add any number of 120 machines to scale your solution
NODE_HOSTS="tallahassee.cs.colostate.edu springfield.cs.colostate.edu santa-fe.cs.colostate.edu salt-lake-city.cs.colostate.edu salem.cs.colostate.edu 
             saint-paul.cs.colostate.edu madison.cs.colostate.edu albany.cs.colostate.edu columbus-oh.cs.colostate.edu columbia.cs.colostate.edu 
             cheyenne.cs.colostate.edu charleston.cs.colostate.edu carson-city.cs.colostate.edu boston.cs.colostate.edu boise.cs.colostate.edu 
             bismarck.cs.colostate.edu baton-rouge.cs.colostate.edu austin.cs.colostate.edu augusta.cs.colostate.edu madison.cs.colostate.edu
             providence.cs.colostate.edu pili.cs.colostate.edu pierre.cs.colostate.edu phoenix.cs.colostate.edu olympia.cs.colostate.edu 
             oklahoma-city.cs.colostate.edu nashville.cs.colostate.edu montpelier.cs.colostate.edu montgomery.cs.colostate.edu raleigh.cs.colostate.edu 
             richmond.cs.colostate.edu sacramento.cs.colostate.edu venus.cs.colostate.edu topeka.cs.colostate.edu trenton.cs.colostate.edu 
             tallahassee.cs.colostate.edu springfield.cs.colostate.edu santa-fe.cs.colostate.edu salt-lake-city.cs.colostate.edu salem.cs.colostate.edu              saint-paul.cs.colostate.edu madison.cs.colostate.edu albany.cs.colostate.edu columbus-oh.cs.colostate.edu columbia.cs.colostate.edu 
             cheyenne.cs.colostate.edu charleston.cs.colostate.edu carson-city.cs.colostate.edu boston.cs.colostate.edu boise.cs.colostate.edu 
             bismarck.cs.colostate.edu baton-rouge.cs.colostate.edu austin.cs.colostate.edu augusta.cs.colostate.edu madison.cs.colostate.edu 
             atlanta.cs.colostate.edu columbus-oh.cs.colostate.edu.cs.colostate.edu concord.cs.colostate.edu indianapolis.cs.colostate.edu jefferson-city.cs.colostate.edu 
             juneau.cs.colostate.edu denver.cs.colostate.edu lansing.cs.colostate.edu lincoln.cs.colostate.edu honolulu.cs.colostate.edu 
             little-rock.cs.colostate.edu helena.cs.colostate.edu hartford.cs.colostate.edu harrisburg.cs.colostate.edu frankfort.cs.colostate.edu 
             dover.cs.colostate.edu des-moines.cs.colostate.edu jackson.cs.colostate.edu saturn.cs.colostate.edu earth.cs.colostate.edu 
             uranus.cs.colostate.edu neptune.cs.colostate.edu mercury.cs.colostate.edu mars.cs.colostate.edu jupiter.cs.colostate.edu
             providence.cs.colostate.edu pili.cs.colostate.edu pierre.cs.colostate.edu phoenix.cs.colostate.edu olympia.cs.colostate.edu 
             oklahoma-city.cs.colostate.edu nashville.cs.colostate.edu montpelier.cs.colostate.edu montgomery.cs.colostate.edu raleigh.cs.colostate.edu 
             richmond.cs.colostate.edu sacramento.cs.colostate.edu venus.cs.colostate.edu topeka.cs.colostate.edu trenton.cs.colostate.edu 
             tallahassee.cs.colostate.edu springfield.cs.colostate.edu santa-fe.cs.colostate.edu salt-lake-city.cs.colostate.edu salem.cs.colostate.edu""


# the port and ip of the registry
# Note: this assumes that the registry is already running before the script is run. 
# SERVER="pili.cs.colostate.edu"
# PORT=9876
# NUM_MSGS=5
# JAR_PATH="~/cs455/455Homework2/build/libs/team8.jar"


# the following command runs the nodes that connect to the registry and send messages
# java -cp ${JAR_PATH} cs455.overlay.Main node ${HOSTNAME} ${REGISTRY_HOST} ${PORT} ${NUM_MSGS} &

for HOSTNAME in ${NODE_HOSTS} ; do
    ssh -l ${USERNAME} ${HOSTNAME} java -cp ${JAR_PATH} cs455.scaling.client.Client ${SERVER}".cs.colostate.edu" ${PORT} ${NUM_MSGS} &
done


# "providence.cs.colostate.edu pili.cs.colostate.edu pierre.cs.colostate.edu phoenix.cs.colostate.edu olympia.cs.colostate.edu 
#             oklahoma-city.cs.colostate.edu nashville.cs.colostate.edu montpelier.cs.colostate.edu montgomery.cs.colostate.edu raleigh.cs.colostate.edu 
#             richmond.cs.colostate.edu sacramento.cs.colostate.edu venus.cs.colostate.edu topeka.cs.colostate.edu trenton.cs.colostate.edu 
#             tallahassee.cs.colostate.edu springfield.cs.colostate.edu santa-fe.cs.colostate.edu salt-lake-city.cs.colostate.edu salem.cs.colostate.edu 
#             saint-paul.cs.colostate.edu madison.cs.colostate.edu albany.cs.colostate.edu columbus-oh.cs.colostate.edu columbia.cs.colostate.edu 
#             cheyenne.cs.colostate.edu charleston.cs.colostate.edu carson-city.cs.colostate.edu boston.cs.colostate.edu boise.cs.colostate.edu 
#             bismarck.cs.colostate.edu baton-rouge.cs.colostate.edu austin.cs.colostate.edu augusta.cs.colostate.edu madison.cs.colostate.edu 
#             atlanta.cs.colostate.edu columbus-oh.cs.colostate.edu.cs.colostate.edu concord.cs.colostate.edu indianapolis.cs.colostate.edu jefferson-city.cs.colostate.edu 
#             juneau.cs.colostate.edu denver.cs.colostate.edu lansing.cs.colostate.edu lincoln.cs.colostate.edu honolulu.cs.colostate.edu 
#             little-rock.cs.colostate.edu helena.cs.colostate.edu hartford.cs.colostate.edu harrisburg.cs.colostate.edu frankfort.cs.colostate.edu 
#             dover.cs.colostate.edu des-moines.cs.colostate.edu jackson.cs.colostate.edu saturn.cs.colostate.edu earth.cs.colostate.edu 
#             uranus.cs.colostate.edu neptune.cs.colostate.edu mercury.cs.colostate.edu mars.cs.colostate.edu jupiter.cs.colostate.edu
#             providence.cs.colostate.edu pili.cs.colostate.edu pierre.cs.colostate.edu phoenix.cs.colostate.edu olympia.cs.colostate.edu 
#             oklahoma-city.cs.colostate.edu nashville.cs.colostate.edu montpelier.cs.colostate.edu montgomery.cs.colostate.edu raleigh.cs.colostate.edu 
#             richmond.cs.colostate.edu sacramento.cs.colostate.edu venus.cs.colostate.edu topeka.cs.colostate.edu trenton.cs.colostate.edu 
#             tallahassee.cs.colostate.edu springfield.cs.colostate.edu santa-fe.cs.colostate.edu salt-lake-city.cs.colostate.edu salem.cs.colostate.edu"
            # kinshasa.cs.colostate.edu tokyo.cs.colostate.edu tehran.cs.colostate.edu singapore.cs.colostate.edu seoul.cs.colostate.edu 
            # santiago.cs.colostate.edu riyadh.cs.colostate.edu madrid.cs.colostate.edu dhaka.cs.colostate.edu hanoi.cs.colostate.edu 
            # jakarta.cs.colostate.edu kabul.cs.colostate.edu lima.cs.colostate.edu london.cs.colostate.edu hong-kong.cs.colostate.edu 
            # bangkok.cs.colostate.edu ankara.cs.colostate.edu baghdad.cs.colostate.edu mexico-city.cs.colostate.edu berlin.cs.colostate.edu"