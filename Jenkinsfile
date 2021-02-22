@Library('Sharedlibraries') import devops.ci.*

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
  // environment {
  //   JENKINSFILE_SCRIPTS_DIR = '.jenkins'
  //   GIT_CREDS_ID = 'github'
  //   APP_CONFIGURATION_JSON_PATH = 'configuration/jenkins.json'
  //   BASEIMAGE_SERVICES_ADMIN_CREDS_ID = 'baseImage_services_AminPassword'
  //   DOCKER_REPOSITORY_CREDS_ID = 'docker_hub'
  //   DOCKER_REPOSITORY_URL = 'https://index.docker.io/v1/'
  //   DOCKER_REPOSITORY_SNAPSHOT_NAME = 'wolfsea89/${projectName}_snapshot'
  //   DOCKER_REPOSITORY_RELEASE_NAME = 'wolfsea89/${projectName}'
  //   PUBLISH_REPOSITORIES = <<JSON>>
  // }
  agent none
  options {
    skipDefaultCheckout true
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
                env.APP_CONFIGURATION_JSON_PATH,
                env.BASEIMAGE_SERVICES_ADMIN_CREDS_ID,
                readJSON(text: env.PUBLISH_REPOSITORIES)
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

              // Git clone repository with code to build
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

              currentBuild.displayName = "#${facts.jobBuildNumber} - ${facts.branchName} - ${facts.versionWithBuildNumber}"
            }
          }
        }
        stage('Prebuild Scripts') {
          parallel {
            stage('Docker'){
              when{
                expression {
                  facts.applicationConfiguration.DOCKER_PROJECTS ? true : false
                }
              }
              steps{
                script{
                  def prebuild = new PrebuildScriptsDocker(this)
                  prebuild.setApplications(facts.applicationConfiguration.DOCKER_PROJECTS)
                  prebuild.setVersion(facts.versionWithBuildNumber)
                  prebuild.setAdminsCredentials(facts.baseImagesAdminCredentialsInService)
                  prebuild.setJenkinsJobInfo(facts.jobName, facts.jobBuildNumber)
                  prebuild.execute()
                }
              }
            }
          }
        }
        stage('Build'){
          parallel {
            stage('Docker'){
              when{
                expression {
                  facts.applicationConfiguration.DOCKER_PROJECTS ? true : false
                }
              }
              steps{
                script{
                  def buildDocker = new DockerBuild(this)
                  buildDocker.setApplications(facts.applicationConfiguration.DOCKER_PROJECTS)
                  buildDocker.setVersion(facts.versionWithBuildNumber)
                  buildDocker.buildProjects()
                  def isReleaseArtefact = (facts.artifactType == "release") ? true : false
                  println(facts.artifactType)
                }
              }
            }
          }
        }
        stage('Publish') {
          parallel {
            stage('DockerHub - Release'){
              when{
                expression {
                  def isDockerProject = (facts.applicationConfiguration.DOCKER_PROJECTS) ? true : false
                  def isReleaseArtefact = (facts.artifactType == "release") ? true : false
                  (isDockerProject && isReleaseArtefact) ? true : false
                }
              }
              steps{
                script{
                  def repository = facts.publishRepositories.DockerHubRelease
                  def publishDocker = new DockerPublish(this)
                  publishDocker.setApplications(facts.applicationConfiguration.DOCKER_PROJECTS)
                  publishDocker.setVersion(facts.versionWithBuildNumber)
                  publishDocker.publish(repository.repositoryUrl, repository.repositoryName, repository.repositoryCredentialID)
                }
              }
            }
            stage('DockerHub - Snapshot'){
              when{
                expression {
                  def isDockerProject = (facts.applicationConfiguration.DOCKER_PROJECTS) ? true : false
                  def isReleaseArtefact = (facts.artifactType == "snapshot") ? true : false
                  (isDockerProject && isReleaseArtefact) ? true : false
                }
              }
              steps{
                script{
                  def repository = facts.publishRepositories.DockerHubSnapshot
                  def publishDocker = new DockerPublish(this)
                  publishDocker.setApplications(facts.applicationConfiguration.DOCKER_PROJECTS)
                  publishDocker.setVersion(facts.versionWithBuildNumber)
                  publishDocker.publish(repository.repositoryUrl, repository.repositoryName, repository.repositoryCredentialID)
                }
              }
            }
            stage('GitHub - Release'){
              when{
                expression {
                  def isDockerProject = (facts.applicationConfiguration.DOCKER_PROJECTS) ? true : false
                  def isReleaseArtefact = (facts.artifactType == "release") ? true : false
                  (isDockerProject && isReleaseArtefact) ? true : false
                }
              }
              steps{
                script{
                  def repository = facts.publishRepositories.GitHubRelease
                  def publishDocker = new DockerPublish(this)
                  publishDocker.setApplications(facts.applicationConfiguration.DOCKER_PROJECTS)
                  publishDocker.setVersion(facts.versionWithBuildNumber)
                  publishDocker.publish(repository.repositoryUrl, repository.repositoryName, repository.repositoryCredentialID)
                }
              }
            }
          }
        }
        post{
          always{
            script{
              def publishDocker = new DockerPublish(this)
              publishDocker.setApplications(facts.applicationConfiguration.DOCKER_PROJECTS)
              publishDocker.setVersion(facts.versionWithBuildNumber)
              publishDocker.clean(repository.repositoryName)
            }
          }
        }
      }
    }
  }
}
