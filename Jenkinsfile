#!groovy
//
// Shared values for different stages:
//
def javaAgent = 'build && java'
def dockerAgent = 'build && docker'
def kubectlAgent = 'deploy && datacenter && linux'

def projectName = 'tsar-planogram'

def devBranch = 'dev'
def stgBranch = 'stg'
def prdBranch = 'prd'

def commit
def branch
def simplifiedBranchName
def repo
def tag

def bitbucketCredentials = 'CIS_BITBUCKET_CREDENTIALS'
// docker harbor config
def dockerCredentials = 'harbor_cloud_registry'
def dockerRepo = 'registry.tools.3stripes.net'
def imagePrefix = 'pea-tsar'

// kubernetes config
def k8sDeploymentYaml = 'k8s_deployment.yaml'

def k8sNamespace_dev = 'tsar-dev'
def k8sNamespace_stg = 'tsar-test'
def k8sNamespace_prd = 'tsar-prod-eu'

@Library(['GlobalJenkinsLibrary@2']) _

//pipeline
node(javaAgent) {
    deleteDir()
    try{
        stage('collect info') {
            checkout scm

            commit = tools.git.getCommitId()
            branch = env.BRANCH_NAME
            jobUrl = env.JOB_URL
            repo = tools.git.getOriginUrl()
            tag = branch

            if(branch == devBranch || branch == stgBranch || branch == prdBranch) {
                simplifiedBranchName = branch
            } else {
                simplifiedBranchName = tools.git.getSimplifiedBranchName()
                if(simplifiedBranchName.length() > 7) {
                    simplifiedBranchName = simplifiedBranchName.substring(0,7)
                }
            }

            notifications.msteams.send message: "BUILDING: ${env.BUILD_URL}",
                credentials: 'tsar-msteams-webhook',
                level: 'info'

            notifications.bitbucket.sendInProgress(bitbucketCredentials, commit)

        }

        stage('config') {
            if (branch == devBranch) {
                withCredentials([file(credentialsId: 'tsar_planogram_app_properties_dev', variable: 'appConfigs')]) {
                    sh '''cp $appConfigs src/main/resources/application.properties'''
                }
            }
            if (branch == stgBranch) {
                withCredentials([file(credentialsId: 'tsar_planogram_app_properties_stg', variable: 'appConfigs')]) {
                    sh '''cp $appConfigs src/main/resources/application.properties'''
                }
            }
            if (branch == prdBranch) {
                withCredentials([file(credentialsId: 'tsar_planogram_app_properties_prd', variable: 'appConfigs')]) {
                    sh '''cp $appConfigs src/main/resources/application.properties'''
                }
            }
        }

        stage('Build') {
            notifications.bitbucket.sendInProgress(bitbucketCredentials, commit)
            env.JAVA_HOME = "${tool 'OpenJDK-11'}"
            env.PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
            tools.maven.run goal: "clean install org.springframework.boot:spring-boot-maven-plugin:repackage -DskipTests"
            sh 'echo "build"'
            stash 'workspace'
        }

        stage('Test') {
            notifications.bitbucket.sendInProgress(bitbucketCredentials, commit)
            tools.maven.run goal: "clean test"
        }

        stage('Sonar') {
            notifications.bitbucket.sendInProgress(bitbucketCredentials, commit)
            env.JAVA_HOME = "${tool 'OpenJDK-11'}"
            env.PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
            tools.sonar.run branch: branch, version: '1.0'
        }

        //Dockerize
        if (branch == devBranch || branch == stgBranch || branch == prdBranch) {
            stage('Dockerize') {
                notifications.bitbucket.sendInProgress(bitbucketCredentials, commit)
                node(dockerAgent) {
                    unstash 'workspace'

                    flows.docker.publish(
                            repo: dockerRepo,
                            image: "${imagePrefix}/${projectName}",
                            credentials: dockerCredentials,
                            tags: [ 'latest', commit ]
                    )
                }
            }
        }

        if (branch == devBranch) {
            stage('deploy-k8s') {
                notifications.bitbucket.sendInProgress(bitbucketCredentials, commit)
                node(kubectlAgent) {
                    deleteDir()
                    unstash 'workspace'

                    def kuberMap = [
                            manifest: k8sDeploymentYaml,
                            namespace: k8sNamespace_dev,
                            placeholderValues: [DEPLOY_IMAGE: "${dockerRepo}/${imagePrefix}/${projectName}:${commit}"]
                    ]

                    withCredentials([file(credentialsId: 'tsar_kube_config_dev', variable: 'KUBECONFIG')]) {
                        flows.k8s.deployDeployment(kuberMap)
                    }

                }
            }

            notifications.bitbucket.sendSuccessful(bitbucketCredentials, commit)
        }

        if (branch == stgBranch) {
            stage('deploy-k8s') {
                notifications.bitbucket.sendInProgress(bitbucketCredentials, commit)
                node(kubectlAgent) {
                    deleteDir()
                    unstash 'workspace'

                    def kuberMap = [
                            manifest: k8sDeploymentYaml,
                            namespace: k8sNamespace_stg,
                            placeholderValues: [DEPLOY_IMAGE: "${dockerRepo}/${imagePrefix}/${projectName}:${commit}"]
                    ]

                    withCredentials([file(credentialsId: 'tsar_kube_config_stg', variable: 'KUBECONFIG')]) {
                        flows.k8s.deployDeployment(kuberMap)
                    }

                }
            }

            notifications.bitbucket.sendSuccessful(bitbucketCredentials, commit)
        }

        if (branch == prdBranch) {
            stage('deploy-k8s') {
                notifications.bitbucket.sendInProgress(bitbucketCredentials, commit)
                node(kubectlAgent) {
                    deleteDir()
                    unstash 'workspace'

                    def kuberMap = [
                            manifest: k8sDeploymentYaml,
                            namespace: k8sNamespace_prd,
                            placeholderValues: [DEPLOY_IMAGE: "${dockerRepo}/${imagePrefix}/${projectName}:${commit}"]
                    ]

                    withCredentials([file(credentialsId: 'tsar_kube_config_prd', variable: 'KUBECONFIG')]) {
                        flows.k8s.deployDeployment(kuberMap)
                    }

                }
            }

            notifications.bitbucket.sendSuccessful(bitbucketCredentials, commit)
        }


        if (branch == devBranch || branch == stgBranch || branch == prdBranch) {
            stage("Promote image") {
                node(dockerAgent) {
                    notifications.bitbucket.send status: 'progress',
                            commit: commit,
                            credentials: bitbucketCredentials,
                            message: "Promoting image to :${tag}"

                    flows.docker.retag([
                            repo: dockerRepo,
                            originalImage: "${imagePrefix}/${projectName}:${commit}",
                            newImage: "${imagePrefix}/${projectName}:${tag}",
                            credentials: dockerCredentials
                    ])
                }
            }
        }

        stage('Complete') {

            notifications.msteams.send message: "SUCCESS: ${env.BUILD_URL}",
                credentials: 'tsar-msteams-webhook',
                level: 'good'

            notifications.bitbucket.sendSuccessful(bitbucketCredentials, commit)
        }
    }
    catch (Exception ex) {

        notifications.msteams.send message: "ERROR: ${env.BUILD_URL}",
            credentials: 'tsar-msteams-webhook',
            level: 'danger'

        notifications.bitbucket.sendFailed(bitbucketCredentials, commit)

        stage('Roll-back') {
            String error = "${ex}"
            echo error
            currentBuild.result = 'FAILURE'
        }
    }
}