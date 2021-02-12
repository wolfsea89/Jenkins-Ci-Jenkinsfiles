def call(Object formParams, Object env ) {
    
    def setfact = [
        'branchPrefix': getBranch(formParams.branch),
        'version': getVersion(formParams.branch, env.BUILD_NUMBER, formParams.manualVersion)
    ]
    println(setfact)
}

def getVersion(String branch, String buildNumber, String manualVersion) {
    
    def version
    String branchPrefix = getBranch(branch)

    if (branchPrefix ==~ /feature|epicfeature|develop/) {
        
        def now = new Date()
        version = "${now.format('yyyy.M.d', TimeZone.getTimeZone('UTC'))}.${now.getDay()}"

    } else if (branchPrefix ==~ /release|hotfix|master/) {

        if (branch ==~ /(.*\/(release|hotfix|master))|(release|hotfix|master)$/){
            
            if (manualVersion ==~ /[0-9]+\.[0-9]+\.[0-9]+$/) {
                version = "${manualVersion}"
            } else {
                error('ERROR: Invalid set manual version')
            }

        } else {
            
            for (def substring in branch.split('/')) {
                
                if (substring ==~ /[0-9]+\.[0-9]+$/) {
                    version = "${substring}.0"
                } else if (substring ==~ /[0-9]+\.[0-9]+\.[0-9]+$/) {
                    version = "${substring}"
                }
            }
        }
    }
    if (version != '') {
        return [
            'semanticVersion': "${version}",
            'semanticVersionWithBuildNumber': "${version}.${buildNumber}"
        ]
    } else {
        error('ERROR: I can\'t set the version')
    }
    
}

def getBranch(String branch) {
    String result
    if (branch ==~ /(.*\/feature)|(feature)\/.*$/) {
        result = 'feature'
    } else if (branch ==~ /(.*\/epicfeature)|(epicfeature)\/.*$/) {
        result = 'epicfeature'
    } else if (branch ==~ /(.*\/develop)|(develop)$/) {
        result = 'develop'
    } else if (branch ==~ /(.*\/release|release)(\/([0-9]+\.[0-9]+|[0-9]+\.[0-9]+\.0)|)$/) {
        result = 'release'
    } else if (branch ==~ /(.*\/hotfix|hotfix)(\/([0-9]+\.[0-9]+\.[0-9]+)|)$/) {
        result = 'hotfix'
    } else if (branch ==~ /(.*\/master)|(master)$/) {
        result = 'master'
    } else {
        error('ERROR: Branch name not compatible with gitflow. Expects value (feature/*, epicfeature/*, develop, release, release/X.Y, release/X.Y.0, hotfix, hotfix/X.Y.Z, master)')
    }
    return result
}