
echo "#############   triggering build now ############"
a=$(curl -s 'http://pankaj:7b4dbe2c6604b847c92586e79d406894@192.168.10.34:8080/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,":",//crumb)')
domain="%20www.lenskart.com%20api.lenskart.com"
b=$(curl -X POST -H "$a" "http://pankaj:7b4dbe2c6604b847c92586e79d406894@192.168.10.34:8080/job/Mobile_Web_Automated_Test_Suite/buildWithParameters?token=9zWM7Gtpt4dmlvzL&email=Msite-QE@lenskart.in&env=13.228.17.254$domain")
echo "############    Execution In Progress, You'll Receive Results @ Msite-QE@lenskart.in ##########"
