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
  agent none
  options {
    skipDefaultCheckout true
  }
  environment {
    jenkinsScripts_directory = '.jenkins'
    gitCredentialId = 'github'
  }
  stages {
    stage('Preparing to work') {
        agent {
          label 'slave_ci_build'
        }
      steps {
        script {
          def gatheringFact = gatheringFact(params, env)
          gitcheckout.application(gatheringFact.branchName, gatheringFact.repositoryUrl, gitCredentialId)
          gitcheckout.jenkinsSripts(gitCredentialId,jenkinsScripts_directory)
          debug()


         
        }
      }
    }
  }
}
