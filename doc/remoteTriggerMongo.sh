echo "#############   triggering build now ############"
a=$(curl -s 'http://lenskart:4852ecba1db6a85d963cce4de86c0239@192.168.10.33:8080/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,":",//crumb)')
echo $a

b=$(curl -X POST -H "$a" "http://lenskart:4852ecba1db6a85d963cce4de86c0239@192.168.10.33:8080/job/BasBhai/buildWithParameters?token=kVfSb7gSm2eDaaFq&channel_type=$1&git_branch=$2&suite_type=$3&host_ip_www.lenskart.com_api.lenskart.com=$4&test_env=$5&email_recipient=$6&api_test_run=$7&scale_execution_via_mongo=$8")
echo "############    Execution In Progress, You'll Receive Results @ $6 ##########"
