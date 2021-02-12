library identifier: 'Jenkins-Sharedlibraries@feature/gatheringFact', retriever: modernSCM([
  $class: 'GitSCMSource',
  remote: 'git@github.com:wolfsea89/Jenkins-Sharedlibraries.git',
  credentialsId: 'github'
])

pipeline {
  parameters {
    string(name: 'branch', defaultValue: 'feature/create_baseimage', description: 'Branch name')
    string(name: 'repositoryUrl', defaultValue: 'git@github.com:wolfsea89/Jenkins-BaseImage.git', description: 'Repository URL (git/https)')
    string(name: 'manualVersion', defaultValue: '', description: 'Set manual version (X.Y.Z). Worked with branch release, hotfix, master without version')
  }
  agent {
    label 'slave_ci_build'
  }
  stages {
    stage('Gathering Fact') {
      steps {        
        script {
          def gatheringFact = gatheringFact([
                  params,
                  env
              ])
          git branch: gatheringFact.branchName, url: gatheringFact.repositoryUrl
          
          pring(gatheringFact)
          sh ( 'ls -la')
        }
      }
    }
  }
}
