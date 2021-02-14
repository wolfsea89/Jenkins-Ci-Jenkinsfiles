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
    JENKINSFILE_SCRIPTS_DIR = '.jenkins'
    GIT_CREDS_ID = 'github'
    APP_CONFIGURATION_JSON_PATH = 'configuration/jenkins.json'
    BASEIMAGE_SERVICES_ADMIN_CREDS_ID = 'baseImage_services_AminPassword'
    DOCKER_REPOSITORY_CREDS_ID = 'docker_hub'
    DOCKER_REPOSITORY_URL = 'https://index.docker.io/v1/'
    DOCKER_REPOSITORY_SNAPSHOT_NAME = 'wolfsea89/jenkins_master_snapshot'
    DOCKER_REPOSITORY_RELEASE_NAME = 'wolfsea89/jenkins_master'
  }
  stages{
    stage('Continuous Integration') {
      agent {
        label 'slave_ci_build'
      }
      stages {
        stage('Preparing to work') {
          steps {
            script {
              deleteDir()
              facts = gatheringFact(params, env)
              
              gitcheckout.application(facts.branchName, facts.repositoryUrl, GIT_CREDS_ID)
              gitcheckout.jenkinsSripts(JENKINSFILE_SCRIPTS_DIR)
              
              facts['applicationConfiguration'] = gatheringFact.applicationConfiguration(env.WORKSPACE + '/' + APP_CONFIGURATION_JSON_PATH)
              currentBuild.displayName = "#${env.BUILD_NUMBER} - ${facts.branchName} - ${facts.version.semanticVersionWithBuildNumber}"
              env.facts = facts

            }
          }
        }
        stage('Prebuild Scripts') {
          steps{
            script{
              prebuildScripts.setVersion(facts)
              prebuildScripts.setCredentials(facts, BASEIMAGE_SERVICES_ADMIN_CREDS_ID)
              prebuildScripts.setJenkinsJobInfo(facts)
            }
          }
        }
        stage('Docker build'){
          when{
            expression {
              facts.applicationConfiguration.DOCKER_PROJECTS ? true : false
            }
          }
          steps{
            script{
              dockerCi.buildProjects(facts.applicationConfiguration.DOCKER_PROJECTS, facts.version.semanticVersionWithBuildNumber)
            }
          }
        }
        stage('Docker publish') {
          parallel {
            stage('Release'){
              when{
                expression {
                  facts.artifactType == "release" ? true : false
                }
              }
              steps{
                script{
                  println("rekease")
                  publishBaseImage(facts.applicationConfiguration.DOCKER_PROJECTS, facts.version, env.DOCKER_REPOSITORY_URL, env.DOCKER_REPOSITORY_RELEASE_NAME, DOCKER_REPOSITORY_CREDS_ID)
                }
              }
            }
            stage('Snapshot'){

              when{
                expression {
                  facts.artifactType == "snapshot" ? true : false
                }
              }
              steps{
                script{
                  publishBaseImage(facts.applicationConfiguration.DOCKER_PROJECTS, facts.version, env.DOCKER_REPOSITORY_URL, env.DOCKER_REPOSITORY_SNAPSHOT_NAME, DOCKER_REPOSITORY_CREDS_ID)
                }
              }
            }
          }
        }
      }
      // post {
      //   always {
      //     deleteDir() /* clean up our workspace */
      //   }
      // }
    }
  }
}
