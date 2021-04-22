@Library('Sharedlibraries')
import devops.ci.*
import devops.ci.dotnet.*
import devops.ci.dotnet.core.*

import groovy.json.JsonSlurper
import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

GatheringFacts facts = new GatheringFacts()

pipeline {
  ////// SET PARAMETERS BY SEED JOB
  parameters {
  //   string(name: 'branchName', defaultValue: 'feature/create_baseimage', description: 'Branch name')
  //   string(name: 'repositoryUrl', defaultValue: 'git@github.com:wolfsea89/Jenkins-BaseImage.git', description: 'Repository URL (git/https)')
    string(name: 'manualVersion', defaultValue: '', description: 'Set manual version (X.Y.Z). Worked with branch release, hotfix, master without version')
  }
  environment {
    BINARY_DIRECTORY = 'b'
    PUBLISH_DIRECTORY = 'p'
    DOTNET_CORE_RUNTIMES = '[ "linux-x64", "win-x64" ]'
    DOTNET_CORE_TEST_RESULTS_DIRECTORY = "TestResults"
    DOTNET_CORE_DISABLE_UNIT_TEST = 'false'
  }
  agent none
  options {
    skipDefaultCheckout true
  }
  stages{
    stage('Continuous Integration') {
      agent {
        label 'slave_ci_build_dotnet_core'
      }
      stages {
        stage('Preparing to work') {
          steps {
            script {
              deleteDir()

              facts.setParametersFromForm(
                params.branchName,
                params.repositoryUrl,
                params.manualVersion
              ).setEnvironments(
                env.JOB_BASE_NAME,
                env.BUILD_NUMBER,
                env.WORKSPACE,
                env.JENKINSFILE_SCRIPTS_DIR,
                env.GIT_CREDS_ID,
                env.APP_CONFIGURATION_JSON_PATH
              ).setDotnetEnvironments(
                env.BINARY_DIRECTORY,
                env.PUBLISH_DIRECTORY,
                readJSON(text: env.DOTNET_CORE_RUNTIMES),
                env.DOTNET_CORE_TEST_RESULTS_DIRECTORY,
                "${env.DOTNET_CORE_DISABLE_UNIT_TEST}"
              ).createVersionWithBuildNumber()

              // Git clone repository with code to build
              checkout([
                $class: 'GitSCM',
                branches: [
                  [ name: branchName ]
                ],
                userRemoteConfigs: [
                  [
                    url: facts.repositoryUrl,
                    credentialsId: facts.gitCredentialId
                  ]
                ]
              ])

              // Git clone repository with scripts to jenkinsfile
              checkout([
                $class: 'GitSCM',
                branches: scm.branches,
                doGenerateSubmoduleConfigurations: scm.doGenerateSubmoduleConfigurations,
                userRemoteConfigs: scm.userRemoteConfigs,
                extensions: [
                  [
                    $class: 'RelativeTargetDirectory',
                    relativeTargetDir: facts.jenkinsScriptDirectory
                  ]
                ],
              ])

              // Read application configuration in Json
              facts.setApplicationConfiguration(readJSON(file: facts.applicationJsonFile))
              currentBuild.displayName = "${facts.jobBuildNumber} - ${facts.branchName} - ${facts.versionWithBuildNumber}"
            }
          }
        }
        stage('Prebuild Scripts') {
          parallel {
            stage('Dotnet Core'){
              when{
                expression {
                  facts.applicationConfiguration.DOTNET_CORE_PROJECTS ? true : false
                }
              }
              steps{
                script{
                  def prebuild = new DotnetAssemblyVersion(this)
                  prebuild.setApplications(facts.applicationConfiguration.DOTNET_CORE_PROJECTS)
                  prebuild.setVersion(facts.versionWithBuildNumber)
                  prebuild.setJenkinsJobInfo(facts.jobName, facts.jobBuildNumber)
                  prebuild.execute()
                }
              }
            }
          }
        }
        stage('Build'){
          parallel {
            stage('Dotnet Core'){
              stages{
                stage('Build Solution'){
                  when{
                    expression {
                      facts.applicationConfiguration.DOTNET_CORE_SOLUTIONS ? true : false
                    }
                  }
                  steps{
                    script{
                      def buildSolutions = new DotnetBuildSolutions(this)
                      buildSolutions.setSolutions(facts.applicationConfiguration.DOTNET_CORE_SOLUTIONS)
                      buildSolutions.setParameters("--configuration Release --verbosity normal")
                      buildSolutions.buildSolutions()
                    }
                  }
                }
                stage('Build Projects'){
                  when{
                    expression {
                      facts.applicationConfiguration.DOTNET_CORE_PROJECTS ? true : false
                    }
                  }
                  steps{
                    script{
                      def buildProjects = new DotnetBuildProjects(this)
                      buildProjects.setProjects(facts.applicationConfiguration.DOTNET_CORE_PROJECTS)
                      buildProjects.setBinaryDirectory(facts.workspace + '/' + facts.binaryDirectory)
                      buildProjects.setPublishDirectory(facts.publishDirectory)
                      buildProjects.setRuntimes(facts.dotnetCoreRuntimes)
                      buildProjects.setParameters("--configuration Release --verbosity normal")
                      buildProjects.buildProjects()
                    }
                  }
                }
              }
            }
          }
        }
        stage('Create Artefact and Tests'){
          parallel {
            stage('Artefact'){
              stages{
                stage('Build Solution'){
                  when{
                    expression {
                      facts.applicationConfiguration.DOTNET_CORE_SOLUTIONS ? true : false
                    }
                  }
                  steps{
                    script{
                      def buildSolutions = new DotnetBuildSolutions(this)
                      buildSolutions.setSolutions(facts.applicationConfiguration.DOTNET_CORE_SOLUTIONS)
                      buildSolutions.setParameters("--configuration Release --verbosity normal")
                      buildSolutions.buildSolutions()
                    }
                  }
                }
                stage('Build Projects'){
                  when{
                    expression {
                      facts.applicationConfiguration.DOTNET_CORE_PROJECTS ? true : false
                    }
                  }
                  steps{
                    script{
                      def buildProjects = new DotnetBuildProjects(this)
                      buildProjects.setProjects(facts.applicationConfiguration.DOTNET_CORE_PROJECTS)
                      buildProjects.setBinaryDirectory(facts.workspace + '/' + facts.binaryDirectory)
                      buildProjects.setPublishDirectory(facts.publishDirectory)
                      buildProjects.setRuntimes(facts.dotnetCoreRuntimes)
                      buildProjects.setParameters("--configuration Release --verbosity normal")
                      buildProjects.buildProjects()
                    }
                  }
                }
              }
            }
            stage('Unit Test'){
              stages{
                stage('Unit Test'){
                  when{
                    expression {
                      def solutionsExist = facts.applicationConfiguration.DOTNET_CORE_SOLUTIONS ? true : false
                      def dotnetCoreProjectsExist = facts.applicationConfiguration.DOTNET_CORE_PROJECTS ? true : false
                      (solutionsExist || dotnetCoreProjectsExist) ? true : false
                    }
                  }
                  steps{
                    script{
                      def dotnetCoreDisableUnitTest = facts.dotnetCoreDisableTest ? true : false
                      if(!dotnetCoreDisableUnitTest){
                        def unitTests = new DotnetUnitTests(this)
                        unitTests.setSolutions(facts.applicationConfiguration.DOTNET_CORE_SOLUTIONS)
                        unitTests.setProjects(facts.applicationConfiguration.DOTNET_CORE_PROJECTS)
                        unitTests.setResultsDirectory(facts.dotnetCoreTestResultsDirectory)
                        unitTests.setParameters('--verbosity normal --logger "trx" --collect "Code Coverage"')
                        unitTests.runUnitTest()
                      } else {
                        unstable('WARNING: Disabled Unit Test')
                      }
                    }
                  }
                }
              }
            }
          }
        }
        // stage('Publish') {
        //   parallel {
        //     stage('DockerHub - Release'){
        //       when{
        //         expression {
        //           Boolean isDockerProject = (facts.applicationConfiguration.DOTNET_CORE_PROJECTS) ? true : false
        //           Boolean isReleaseArtefact = (facts.artifactType == "release") ? true : false
        //           (isDockerProject && isReleaseArtefact) ? true : false
        //         }
        //       }
        //       steps{
        //         script{
        //           def repository = facts.publishRepositories.DockerHubRelease
        //           def publishDocker = new DockerPublish(this)
        //           publishDocker.setApplications(facts.applicationConfiguration.DOTNET_CORE_PROJECTS)
        //           publishDocker.setVersion(facts.versionWithBuildNumber)
        //           publishDocker.publish(repository.repositoryUrl, repository.repositoryName, repository.repositoryCredentialID)
        //         }
        //       }
        //     }
        //     stage('DockerHub - Snapshot'){
        //       when{
        //         expression {
        //           Boolean isDockerProject = (facts.applicationConfiguration.DOTNET_CORE_PROJECTS) ? true : false
        //           Boolean isReleaseArtefact = (facts.artifactType == "snapshot") ? true : false
        //           (isDockerProject && isReleaseArtefact) ? true : false
        //         }
        //       }
        //       steps{
        //         script{
        //           def repository = facts.publishRepositories.DockerHubSnapshot
        //           def publishDocker = new DockerPublish(this)
        //           publishDocker.setApplications(facts.applicationConfiguration.DOTNET_CORE_PROJECTS)
        //           publishDocker.setVersion(facts.versionWithBuildNumber)
        //           publishDocker.publish(repository.repositoryUrl, repository.repositoryName, repository.repositoryCredentialID)
        //         }
        //       }
        //     }
        //     stage('GitHubRelease'){
        //       when{
        //         expression {
        //           Boolean isDockerProject = (facts.applicationConfiguration.DOTNET_CORE_PROJECTS) ? true : false
        //           Boolean isReleaseArtefact = (facts.artifactType == "release") ? true : false
        //           (isDockerProject && isReleaseArtefact) ? true : false
        //         }
        //       }
        //       steps{
        //         script{
        //           def repository = facts.publishRepositories.GitHubRelease
        //           def publishDocker = new DockerPublish(this)
        //           publishDocker.setApplications(facts.applicationConfiguration.DOTNET_CORE_PROJECTS)
        //           publishDocker.setVersion(facts.versionWithBuildNumber)
        //           publishDocker.publish(repository.repositoryUrl, repository.repositoryName, repository.repositoryCredentialID)
        //         }
        //       }
        //     }
        //   }
        // }
      }
      // post{
      //   always{
      //     script{
      //       def repository = facts.publishRepositories
      //       def publishDocker = new DockerPublish(this)
      //       publishDocker.setApplications(facts.applicationConfiguration.DOTNET_CORE_PROJECTS)
      //       publishDocker.setVersion(facts.versionWithBuildNumber)
      //       publishDocker.clean()
      //       publishDocker.clean(repository.DockerHubRelease.repositoryName)
      //       publishDocker.clean(repository.DockerHubSnapshot.repositoryName)
      //       publishDocker.clean(repository.GitHubRelease.repositoryName)
      //     }
      //   }
      // }
    }
  }
}
