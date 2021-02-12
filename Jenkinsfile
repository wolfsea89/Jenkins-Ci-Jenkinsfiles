library identifier: 'Jenkins-Sharedlibraries@develop', retriever: modernSCM([
  $class: 'GitSCMSource',
  remote: 'git@github.com:wolfsea89/Jenkins-Sharedlibraries.git',
  credentialsId: 'github'
])

pipeline {
  parameters {
    string(name: 'branch', defaultValue: 'develop', description: 'Branch name')
    string(name: 'repositoryUrl', defaultValue: '', description: 'Repository URL (git/https)')
    string(name: 'manualVersion', defaultValue: '', description: 'Set manual version (X.Y.Z). Worked with branch release, hotfix, master without version')
  }
  agent {
    label 'slave_ci_build'
  }
  stages {
    stage('Gathering Fact') {
      steps {
        script {
          def a = gatheringFact([
                  params,
                  env
              ])
          // pring(a)
        }
      }
    }
  }
}
