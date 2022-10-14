#!groovy
//
// Shared values for different stages:
//
def dockerjavaAgent = 'ubuntu_java-11_docker-20'
def kubectlAgent ='ubuntu_kubectl-1.22'

def projectName = 'tsar-planogram'

def devBranch = 'dev'
def stgBranch = 'stg'
def prdBranch = 'prd'

def commit
def branch
def simplifiedBranchName
def repo
def tag

def kubeConfig_dev = 'tsar_kube_config_dev'
def kubeConfig_stg = 'tsar_kube_config_stg'
def kubeConfig_prd = 'tsar_kube_config_prd'

def bitbucketCredentials = 'CIS_BITBUCKET_CREDENTIALS'
// docker harbor config
def dockerCredentials = 'HARBOR_CLOUD'
def dockerRepo = 'registry.tools.3stripes.net'
def imagePrefix = 'pea-tsar'

// kubernetes config
def k8sDeploymentYaml = 'k8s_deployment.yaml'

def k8sNamespace_dev = 'tsar-dev'
def k8sNamespace_stg = 'tsar-test'
def k8sNamespace_prd = 'tsar-prod-eu'

@Library(['GlobalJenkinsLibrary@2']) _

//pipeline
node(dockerjavaAgent) {
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

            tools.sonar.run( env: 'COMMUNITY-PRD', version: '1.0', branch: branch )
        }

        //Dockerize
        if (branch == devBranch || branch == stgBranch || branch == prdBranch) {
            stage('Dockerize') {
                notifications.bitbucket.sendInProgress(bitbucketCredentials, commit)
                unstash 'workspace'

                flows.docker.publish(
                        repo: dockerRepo,
                        image: "${imagePrefix}/${projectName}",
                        credentials: dockerCredentials,
                        tags: [ 'latest', commit ]
                )
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

                    tools.k8s.withKubeconfig(kubeConfig_dev){
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

                    tools.k8s.withKubeconfig(kubeConfig_stg){
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

                    tools.k8s.withKubeconfig(kubeConfig_prd){
                        flows.k8s.deployDeployment(kuberMap)
                    }

                }
            }

            notifications.bitbucket.sendSuccessful(bitbucketCredentials, commit)
        }


        if (branch == devBranch || branch == stgBranch || branch == prdBranch) {
            stage("Promote image") {
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