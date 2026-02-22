allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

val newBuildDir: Directory =
    rootProject.layout.buildDirectory
        .dir("../../build")
        .get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    val newSubprojectBuildDir: Directory = newBuildDir.dir(project.name)
    project.layout.buildDirectory.value(newSubprojectBuildDir)

    afterEvaluate {
        if (pluginManager.hasPlugin("com.android.library")) {
            extensions.getByType<com.android.build.gradle.LibraryExtension>().apply {
                if (namespace == null) {
                    val manifestFile = sourceSets.getByName("main").manifest.srcFile
                    if (manifestFile.exists()) {
                        val doc = javax.xml.parsers.DocumentBuilderFactory.newInstance()
                            .newDocumentBuilder()
                            .parse(manifestFile)
                        val packageName = doc.documentElement.getAttribute("package")
                        if (packageName.isNotEmpty()) {
                            namespace = packageName
                        }
                    }
                }
            }
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
