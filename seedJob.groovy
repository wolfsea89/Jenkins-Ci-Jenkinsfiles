def jobs = readFileFromWorkspace('jobs.json')
// def view = 

job('example-1') {
    steps {
        def test = readJSON text: jobs
        println(test)
    }
}


 
// def jsonSlurper = new JsonSlurper()
// data = jsonSlurper.parse(new File("jobs.json"))
// println(data)
// println ("WSK")