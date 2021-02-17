import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*
import java.util.Arrays
import java.util.Lists
import java.util.stream.Collectors
import java.util.stream.Stream
import groovy.json.*
 
 
println (env.WORKSPACE)

 
// def jsonSlurper = new JsonSlurper()
// data = jsonSlurper.parse(new File("jobs.json"))
// println(data)
// println ("WSK")