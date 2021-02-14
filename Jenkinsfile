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
    applicationConfigurationInProjectJsonPath = 'configuration/jenkins.json'
    baseImage_services_Admin_credentialId = 'baseImage_services_AminPassword'
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
              facts = gatheringFact(params, env)
              
              gitcheckout.application(facts.branchName, facts.repositoryUrl, gitCredentialId)
              gitcheckout.jenkinsSripts(jenkinsScripts_directory)
              
              facts['applicationConfiguration'] = gatheringFact.applicationConfiguration(env.WORKSPACE + '/' + applicationConfigurationInProjectJsonPath)
              currentBuild.displayName = "#${env.BUILD_NUMBER} - ${facts.branchName} - ${facts.version.semanticVersionWithBuildNumber}"
              env.facts = facts

            }
          }
        }
        stage('Prebuild Scripts') {
          steps{
            script{
              prebuildScripts.setVersion(facts)
              prebuildScripts.setCredentials(facts, baseImage_services_Admin_credentialId)
              prebuildScripts.setJenkinsJobParams(facts)
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
              dockerCi.buildProjects(facts.applicationConfiguration.DOCKER_PROJECTS,facts.version.semanticVersionWithBuildNumber)
              println(facts.artifactType)
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
                  // dockerCi.buildProjects(facts.applicationConfiguration.DOCKER_PROJECTS,facts.version.semanticVersionWithBuildNumber)
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
                  println("snapshot")
                  // dockerCi.buildProjects(facts.applicationConfiguration.DOCKER_PROJECTS,facts.version.semanticVersionWithBuildNumber)
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
