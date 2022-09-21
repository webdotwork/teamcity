import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.04"

project {

    vcsRoot(Hlplayer)

    buildType(Build)
}

object Build : BuildType({
    name = "Build"

    artifactRules = "src => src"
    publishArtifacts = PublishMode.SUCCESSFUL

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            name = "Test"

            conditions {
                doesNotContain("teamcity.build.branch", "main")
            }
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            userSettingsSelection = "settings.xml"
        }
        maven {

            conditions {
                equals("teamcity.build.branch", "main")
            }
            goals = "clean deploy"
            userSettingsSelection = "settings.xml"
        }
    }

    triggers {
        vcs {
        }
    }
})

object Hlplayer : GitVcsRoot({
    name = "hlplayer"
    url = "git@github.com:webdotwork/teamcity.git"
    branch = "main"
    branchSpec = "+:refs/heads/*"
    authMethod = uploadedKey {
        uploadedKey = "git"
    }
})
