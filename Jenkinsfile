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
          
          println(env)

              println(env.BRANCH_NAME)
              println(env.CHANGE_ID)
println(env.CHANGE_URL)
println(env.CHANGE_TITLE)
println(env.CHANGE_AUTHOR)
println(env.CHANGE_AUTHOR_DISPLAY_NAME)
println(env.CHANGE_AUTHOR_EMAIL)
println(env.CHANGE_TARGET)
println(env.CHANGE_BRANCH)
println(env.CHANGE_FORK)
println(env.TAG_NAME)
println(env.TAG_TIMESTAMP)
println(env.TAG_UNIXTIME)
println(env.TAG_DATE)
println(env.BUILD_NUMBER)
println(env.BUILD_ID)
println(env.BUILD_DISPLAY_NAME)
println(env.JOB_NAME)
println(env.JOB_BASE_NAME)
println(env.BUILD_TAG)
println(env.EXECUTOR_NUMBER)
println(env.NODE_NAME)
println(env.NODE_LABELS)
println(env.WORKSPACE)
println(env.WORKSPACE_TMP)
println(env.JENKINS_HOME)
println(env.JENKINS_URL)
println(env.BUILD_URL)
println(env.JOB_URL)
println(env.GIT_COMMIT)
println(env.GIT_PREVIOUS_COMMIT)
println(env.GIT_PREVIOUS_SUCCESSFUL_COMMIT))
println(env.GIT_BRANCH)
println(env.GIT_LOCAL_BRANCH)
println(env.GIT_CHECKOUT_DIR)
println(env.GIT_URL)
println(env.GIT_COMMITTER_NAME)
println(env.GIT_AUTHOR_NAME)
println(env.GIT_COMMITTER_EMAIL)
println(env.GIT_AUTHOR_EMAIL)
"""
          
        }
      }
    }
  }
}
