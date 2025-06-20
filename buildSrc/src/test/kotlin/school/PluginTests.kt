@file:Suppress("MemberVisibilityCanBePrivate")

package school

import com.fasterxml.jackson.databind.json.JsonMapper
import org.apache.commons.lang3.SystemUtils.USER_HOME_KEY
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.junit.jupiter.api.assertDoesNotThrow
import school.GradleTestUtils.captureOutput
import school.GradleTestUtils.displayPersonDataSchemaStructure
import school.GradleTestUtils.displayWorkspaceDataSchemaStructure
import school.GradleTestUtils.displayWorkspaceStructure
import school.GradleTestUtils.initWorkspace
import school.GradleTestUtils.projectInstance
import school.GradleTestUtils.releaseOutput
import school.PluginTests.Workspace.WorkspaceEntry
import school.PluginTests.Workspace.WorkspaceEntry.CollaborationEntry.Collaboration
import school.PluginTests.Workspace.WorkspaceEntry.CommunicationEntry.Communication
import school.PluginTests.Workspace.WorkspaceEntry.ConfigurationEntry.Configuration
import school.PluginTests.Workspace.WorkspaceEntry.CoreEntry.Education
import school.PluginTests.Workspace.WorkspaceEntry.CoreEntry.Education.EducationEntry.*
import school.PluginTests.Workspace.WorkspaceEntry.DashboardEntry.Dashboard
import school.PluginTests.Workspace.WorkspaceEntry.JobEntry.Job
import school.PluginTests.Workspace.WorkspaceEntry.JobEntry.Job.HumanResourcesEntry.Position
import school.PluginTests.Workspace.WorkspaceEntry.JobEntry.Job.HumanResourcesEntry.Resume
import school.PluginTests.Workspace.WorkspaceEntry.OfficeEntry.Office
import school.PluginTests.Workspace.WorkspaceEntry.OfficeEntry.Office.LibraryEntry.*
import school.PluginTests.Workspace.WorkspaceEntry.OrganisationEntry.Organisation
import school.PluginTests.Workspace.WorkspaceEntry.PortfolioEntry.Portfolio
import school.PluginTests.Workspace.WorkspaceEntry.PortfolioEntry.Portfolio.PortfolioProject
import school.PluginTests.Workspace.WorkspaceEntry.PortfolioEntry.Portfolio.PortfolioProject.ProjectBuild
import school.training.content.*
import forms.FormPlugin
import school.frontend.SchoolPlugin
import school.frontend.SchoolPlugin.Companion.TASK_HELLO
import jbake.JBakeGhPagesPlugin
import school.training.teacher.FPAManager.spgJsonMapper
import school.training.teacher.FPAManager.toJson
import school.training.teacher.FPAManager.toYaml
import workspace.OfficeEntry
import java.io.File
import java.lang.System.out
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


//Deskboard-Bibliotheque-Tiroir-Thematique-Dossier
class PluginTests {
/*
@file:Suppress("unused")

package app.workspace

import app.workspace.Workspace.WorkspaceEntry.OfficeEntry.Office
import app.workspace.Workspace.WorkspaceEntry.OfficeEntry.Office.LibraryEntry.Slides
import java.nio.file.Path

data class Workspace(val entries: WorkspaceEntry) {
    data class WorkspaceEntry(
        val name: String,
        val path: String,
        val office: Office,
        val cores: Map<String, CoreEntry>,
        val job: JobEntry,
        val configuration: ConfigurationEntry,
        val communication: CommunicationEntry,
        val organisation: OrganisationEntry,
        val collaboration: CollaborationEntry,
        val dashboard: DashboardEntry,
        val portfolio: PortfolioEntry,
    ) {
        sealed interface PortfolioEntry {
            data class Portfolio(val project: MutableMap<String, PortfolioProject>) :
                PortfolioEntry {
                data class PortfolioProject(
                    val name: String,
                    val cred: String,
                    val builds: MutableMap<String, ProjectBuild>
                ) {
                    data class ProjectBuild(val name: String)
                }
            }
        }

        sealed interface CoreEntry {
            data class Education(
                val path: String = "education",
                val school: EducationEntry,
                val student: EducationEntry,
                val teacher: EducationEntry,
                val educationTools: EducationEntry,
            ) : CoreEntry {
                sealed class EducationEntry {
                    data class Student(val name: String) : EducationEntry()
                    data class Teacher(val name: String) : EducationEntry()
                    data class School(val name: String) : EducationEntry()
                    data class EducationTools(val name: String) : EducationEntry()
                }
            }
        }

        sealed interface OfficeEntry {
            data class Office(
                val path: String = "office",
                val books: LibraryEntry,
                val datas: LibraryEntry,
                val formations: LibraryEntry,
                val bizness: LibraryEntry,
                val notebooks: LibraryEntry,
                val pilotage: LibraryEntry,
                val schemas: LibraryEntry,
                val slides: Slides,
                val sites: LibraryEntry,
            ) : OfficeEntry {
                sealed class LibraryEntry {
                    data class Books(val name: String) : LibraryEntry()
                    data class Datas(val name: String) : LibraryEntry()
                    data class TrainingCatalogue(val catalogue: String) : LibraryEntry()
                    data class Notebooks(val notebooks: String) : LibraryEntry()
                    data class Pilotage(val name: String) : LibraryEntry()
                    data class Schemas(val name: String) : LibraryEntry()
                    data class Slides(val path: String) : LibraryEntry()
                    data class Sites(val name: String) : LibraryEntry()
                    data class Profession(val name: String) : LibraryEntry()
                }
            }
        }

        sealed interface JobEntry {
            data class Job(
                val path: String = "job",
                val position: HumanResourcesEntry,
                val resume: HumanResourcesEntry
            ) : JobEntry {
                sealed class HumanResourcesEntry {
                    data class Resume(val name: String) : HumanResourcesEntry()
                    data class Position(val name: String) : HumanResourcesEntry()
                }
            }
        }

        sealed interface ConfigurationEntry {
            data class Configuration(val path: String = "configuration", val configuration: String) :
                ConfigurationEntry
        }

        sealed interface CommunicationEntry {

            data class Communication(val path: String = "communication", val site: String) :
                CommunicationEntry
        }

        sealed interface OrganisationEntry {
            data class Organisation(val organisation: String) : OrganisationEntry
        }

        sealed interface CollaborationEntry {
            data class Collaboration(val collaboration: String) : CollaborationEntry
        }

        sealed interface DashboardEntry {
            data class Dashboard(val dashboard: String) : DashboardEntry
        }
    }


    enum class InstallationType {
        ALL_IN_ONE,
        SEPARATED_FOLDERS
    }

    data class WorkspaceConfig(
        val basePath: Path,
        val type: InstallationType,
        val subPaths: Map<String, Path> = emptyMap(),
        val configFileName: String = "config.yaml"
    )

    companion object {
        fun install(path: String) = println("Installing workspace on path : $path")
    }
}
*/
    data class Workspace(val workspace: WorkspaceEntry) {
        data class WorkspaceEntry(
            val name: String,
            val office: Office,
            val cores: Map<String, CoreEntry>,
            val job: JobEntry,
            val configuration: ConfigurationEntry,
            val communication: CommunicationEntry,
            val organisation: OrganisationEntry,
            val collaboration: CollaborationEntry,
            val dashboard: DashboardEntry,
            val portfolio: PortfolioEntry,
        ) {
            sealed interface PortfolioEntry {
                data class Portfolio(val project: MutableMap<String, PortfolioProject>) : PortfolioEntry {
                    data class PortfolioProject(
                        val name: String,
                        val cred: String,
                        val builds: MutableMap<String, ProjectBuild>
                    ) {
                        data class ProjectBuild(val name: String)
                    }
                }
            }

            sealed interface CoreEntry {
                data class Education(
                    val school: EducationEntry,
                    val student: EducationEntry,
                    val teacher: EducationEntry,
                    val educationTools: EducationEntry,
                ) : CoreEntry {
                    sealed class EducationEntry {
                        data class Student(val name: String) : EducationEntry()
                        data class Teacher(val name: String) : EducationEntry()
                        data class School(val name: String) : EducationEntry()
                        data class EducationTools(val name: String) : EducationEntry()
                    }
                }
            }

            sealed interface OfficeEntry {
                data class Office(
                    val books: LibraryEntry,
                    val datas: LibraryEntry,
                    val formations: LibraryEntry,
                    val bizness: LibraryEntry,
                    val notebooks: LibraryEntry,
                    val pilotage: LibraryEntry,
                    val schemas: LibraryEntry,
                    val slides: Slides,
                    val sites: LibraryEntry,
                ) : OfficeEntry {
                    sealed class LibraryEntry {
                        data class Books(val name: String) : LibraryEntry()
                        data class Datas(val name: String) : LibraryEntry()
                        data class TrainingCatalogue(val catalogue: String) : LibraryEntry()
                        data class Notebooks(val notebooks: String) : LibraryEntry()
                        data class Pilotage(val name: String) : LibraryEntry()
                        data class Schemas(val name: String) : LibraryEntry()
                        data class Slides(val path: String) : LibraryEntry()
                        data class Sites(val name: String) : LibraryEntry()
                        data class Profession(val name: String) : LibraryEntry()
                    }
                }
            }

            sealed interface JobEntry {
                data class Job(
                    val position: HumanResourcesEntry,
                    val resume: HumanResourcesEntry
                ) : JobEntry {
                    sealed class HumanResourcesEntry {
                        data class Resume(val name: String) : HumanResourcesEntry()
                        data class Position(val name: String) : HumanResourcesEntry()
                    }
                }
            }

            sealed interface ConfigurationEntry {
                data class Configuration(val configuration: String) : ConfigurationEntry
            }

            sealed interface CommunicationEntry {
                data class Communication(val site: String) : CommunicationEntry
            }

            sealed interface OrganisationEntry {
                data class Organisation(val organisation: String) : OrganisationEntry
            }

            sealed interface CollaborationEntry {
                data class Collaboration(val collaboration: String) : CollaborationEntry
            }

            sealed interface DashboardEntry {
                data class Dashboard(val dashboard: String) : DashboardEntry
            }
        }
    }

    companion object {
        private const val SPG_PATH =
            "/workspace/__repositories__/workspace-engine/buildSrc/src/main/resources/training_8.json"
        private const val SPGS_PATH =
            "/workspace/__repositories__/workspace-engine/buildSrc/src/main/resources/trainings.json"

        /**
         * return the project workspace configuration
         */
        @JvmStatic
        val Project.workspaceWrapper: Workspace
            get() = TODO("Not yet implemented")

        @JvmStatic
        val Project.testConfiguration: Workspace
            get() = Workspace(
                workspace = WorkspaceEntry(
                    name = "fonderie",
                    cores = mapOf(
                        "education" to Education(
                            school = School(name = "talaria"),
                            student = Student(name = "olivier"),
                            teacher = Teacher(name = "cheroliv"),
                            educationTools = EducationTools(name = "edTools")
                        ),
                    ),
                    job = Job(
                        position = Position("Teacher"),
                        resume = Resume(name = "CV")
                    ),
                    configuration = Configuration(configuration = "school-configuration"),
                    communication = Communication(site = "static-website"),
                    office = Office(
                        books = Books(name = "books-collection"),
                        datas = Datas(name = "datas"),
                        formations = TrainingCatalogue(catalogue = "formations"),
                        bizness = Profession("bizness"),
                        notebooks = Notebooks(notebooks = "notebooks"),
                        pilotage = Pilotage(name = "pilotage"),
                        schemas = Schemas(name = "schemas"),
                        slides = Slides(path = "${System.getProperty("user.home")}/workspace/office/slides"),
                        sites = Sites(name = "sites")
                    ),
                    organisation = Organisation(organisation = "organisation"),
                    collaboration = Collaboration(collaboration = "collaboration"),
                    dashboard = Dashboard(dashboard = "dashboard"),
                    portfolio = Portfolio(
                        mutableMapOf(
                            "school" to PortfolioProject(
                                name = "name",
                                cred = "credential",
                                builds = mutableMapOf("training" to ProjectBuild(name = "training"))
                            )
                        )
                    )
                ),
            )
    }

    @Test
    fun `play with training, spg, spd`(): Unit = assertDoesNotThrow {
        SPG_PATH.run {
            SPG().toJson.run(::println)
            SPG().toYaml.run(::println)

            Training(
                spg = USER_HOME_KEY
                    .run(System::getProperty)
                    .let { "$it$this" }
                    .run(::File)
                    .readText()
                    .spgJsonMapper,
                pilotage = QuintilianApproach()
            ).toJson.apply(::println)

            USER_HOME_KEY
                .run(System::getProperty)
                .let { "$it$this" }
                .run(::File)
                .readText()
                .spgJsonMapper
                .run(::println)

            USER_HOME_KEY
                .run(System::getProperty)
                .let { "$it${SPGS_PATH}" }
                .run(::File)
                .readText()
                .run {
                    JsonMapper()
                        .readerForListOf(SPG::class.java)
                        .readValue<List<SPG>>(this)
                }
                .forEach(::println)
        }
    }

    @Test
    fun `test Workspace structure`(): Unit = assertDoesNotThrow {
        projectInstance.displayWorkspaceStructure()
        projectInstance.displayWorkspaceDataSchemaStructure()
    }


    @Test
    fun `test when loading with workspaceWrapper ext fun of the project if yaml config is provided`(): Unit =
        assertDoesNotThrow {
            assertEquals(
                "${System.getProperty("user.home")}/workspace/office/slides",
                projectInstance.testConfiguration.workspace.office.slides.path
            )
        }


    @Test
    fun `test when yaml config is not provided`(): Unit = assertDoesNotThrow {
        projectInstance
    }


    @Test
    fun `test when yaml config is provided`(): Unit = assertDoesNotThrow {
        projectInstance
    }


    @Test
    fun `test Person structure`(): Unit = assertDoesNotThrow {
        projectInstance.displayPersonDataSchemaStructure()
    }

    @Test
    fun checkInitWorkspace(): Unit = initWorkspace
        .run(workspace.Office::isEmpty)
        .run(::assertTrue)

    /**
     * Workspace est une map et cette map a besoin de fonctionnalités.
     *
     * - Ajouter une paire clé/map au bout du chemin de clé dans un list
     * - Supprimer une paire clé/map au bout du chemin de clé dans un list
     * - Ajouter une paire clé/valeur sur un chemin de clé dans une list de string
     * - Trouver une valeur par chemin de clé
     * - Mettre à jour une valeur par chemin de clé
     *
     */
    @Test
    fun checkAddEntryToWorkspace(): Unit {
        fun workspace.Office.addEntry(entry: OfficeEntry) {
//        put(entry.first.last(),entry.second)
        }

        val ws: workspace.Office = initWorkspace
        ws.addEntry(
            listOf(
                "workspace",
                "portfolio",
                "projects",
                "form",
                "cred"
            ) to "/home/foo/workspace/bureau/cred.json"
        )
    }

    @Test
    fun `From FormPlugin, check 'form' task render expected message`(): Unit {
        val outputStreamCaptor = captureOutput
        projectInstance.run {
            apply<FormPlugin>()
            "isFormAuthOk".let(tasks::findByName)!!
            outputStreamCaptor
                .toString()
                .trim()
                .let { "Output: $it" }
                .let(::println)
        }
//        assertEquals(
//            "Task :isFormAuthOk",

//        )
        out.releaseOutput
    }

    @Test
    fun `From FormPlugin, check 'form' task exists`(): Unit {
        projectInstance.run {
            apply<FormPlugin>()
            assertNotNull("isFormAuthOk".let(tasks::findByName))
        }
    }


    @Test
    fun `From SchoolPlugin, check 'hello' task exists`(): Unit {
        projectInstance.run {
            apply<SchoolPlugin>()
            assertNotNull(TASK_HELLO.let(tasks::findByName))
        }
    }

    @Test
    fun `From JBakeGhPagesPlugin, check 'helloJBakeGhPages' task exists`(): Unit {
        projectInstance.run {
            apply<JBakeGhPagesPlugin>()
            assertNotNull("helloJBakeGhPages".let(tasks::findByName))
        }
    }
}