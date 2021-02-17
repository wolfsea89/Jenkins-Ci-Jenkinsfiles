// import jenkins.*
// import jenkins.model.*
// import hudson.*
// import hudson.model.*
// import java.util.Arrays
// import java.util.List
// import java.util.stream.Collectors
// import java.util.stream.Stream
// import groovy.json.*
 

// import jenkins.automation.utils.ScmUtils
// import jenkins.automation.utils.EnvironmentUtils

def environmentVarsConfigFile = readFileFromWorkspace('jobs.json')

 
println (environmentVarsConfigFile)

 
// def jsonSlurper = new JsonSlurper()
// data = jsonSlurper.parse(new File("jobs.json"))
// println(data)
// println ("WSK")