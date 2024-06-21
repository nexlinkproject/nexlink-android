package com.nexlink.nexlinkmobileapp.data.remote.retrofit

import com.nexlink.nexlinkmobileapp.data.remote.request.auth.LoginRequest
import com.nexlink.nexlinkmobileapp.data.remote.request.auth.SignUpRequest
import com.nexlink.nexlinkmobileapp.data.remote.request.projects.CreateProjectRequest
import com.nexlink.nexlinkmobileapp.data.remote.request.tasks.CreateTasksRequest
import com.nexlink.nexlinkmobileapp.data.remote.response.auth.LoginResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.auth.SignUpResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.ml.FeedbackResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.ml.ScheduleResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.AllProjectsResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.DeleteProjectResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.GetProjectTasksResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.GetProjectUsersResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.OneProjectResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.OneProjectUpdateResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.tasks.AllTasksResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.tasks.DeleteTaskResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.tasks.GetTaskUsersResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.tasks.OneTaskResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.tasks.UpdateTaskResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.users.AddUserToProjectResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.users.AddUserToTaskResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.users.AllUsersResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.users.OneUserResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ======= AUTH ENDPOINTS =======
    @Headers("Content-Type: application/json")
    @POST("auth/login")
    suspend fun login(
        @Body login: LoginRequest,
    ): LoginResponse

    @Headers("Content-Type: application/json")
    @POST("auth/register")
    suspend fun register(
        @Body register: SignUpRequest,
    ): SignUpResponse

    // ======= PROJECTS ENDPOINTS =======
    @GET("projects")
    suspend fun getProjects(
        @Query("status") status: String? = null,
    ): AllProjectsResponse

//    @GET("projects")
//    suspend fun getProjectsWithUserId(
//        @Query("status") status: String? = null,
//    ): AllProjectsResponse

    @GET("projects/{projectId}")
    suspend fun getProjectById(
        @Path("projectId") projectId: String,
    ): OneProjectResponse

    @Headers("Content-Type: application/json")
    @POST("projects")
    suspend fun createProjects(
        @Body project: CreateProjectRequest,
    ): OneProjectResponse

    @Headers("Content-Type: application/json")
    @PUT("projects/{projectId}")
    suspend fun updateProject(
        @Path("projectId") projectId: String,
        @Body project: CreateProjectRequest
    ): OneProjectUpdateResponse

    @DELETE("projects/{projectId}")
    suspend fun deleteProject(
        @Path("projectId") projectId: String
    ): DeleteProjectResponse

    @GET("projects/{projectId}/users")
    suspend fun getProjectUsers(
        @Path("projectId") projectId: String
    ): GetProjectUsersResponse

    @GET("tasks/project/{projectId}")
    suspend fun getProjectTasks(
        @Path("projectId") projectId: String
    ): GetProjectTasksResponse

    // ======= USERS ENDPOINTS =======
    @GET("users")
    suspend fun getAllUsers(
    ): AllUsersResponse

    @GET("users/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: String,
    ): OneUserResponse

    @Headers("Content-Type: application/json")
    @POST("projects/{projectId}/users/{userId}")
    suspend fun addUserToProject(
        @Path("userId") userId: String,
        @Path("projectId") projectId: String,
    ): AddUserToProjectResponse

    @Headers("Content-Type: application/json")
    @POST("tasks/{taskId}/users/{userId}")
    suspend fun addUserToTask(
        @Path("userId") userId: String,
        @Path("taskId") taskId: String,
    ): AddUserToTaskResponse

    @DELETE("projects/{projectId}/users/{userId}")
    suspend fun removeUserFromProject(
        @Path("userId") userId: String,
        @Path("projectId") projectId: String,
    ): DeleteProjectResponse

    @DELETE("tasks/{taskId}/users/{userId}")
    suspend fun removeUserFromTask(
        @Path("userId") userId: String,
        @Path("taskId") taskId: String,
    ): DeleteTaskResponse

    // ======= TASKS ENDPOINTS =======
    @GET("tasks")
    suspend fun getAllTasks(
    ): AllTasksResponse

    @GET("tasks/{taskId}")
    suspend fun getTaskById(
        @Path("taskId") taskId: String,
    ): OneTaskResponse

    @DELETE("tasks/{taskId}")
    suspend fun deleteTask(
        @Path("taskId") taskId: String
    ): DeleteTaskResponse

    @Headers("Content-Type: application/json")
    @POST("tasks")
    suspend fun createTasks(
        @Body task: CreateTasksRequest,
    ): OneTaskResponse

    @Headers("Content-Type: application/json")
    @PUT("tasks/{taskId}")
    suspend fun updateTask(
        @Path("taskId") projectId: String,
        @Body task: CreateTasksRequest
    ): UpdateTaskResponse

    @Headers("Content-Type: application/json")
    @PUT("tasks/{taskId}")
    suspend fun updateTaskPartial(
        @Path("taskId") taskId: String,
        @Body fields: Map<String, @JvmSuppressWildcards Any>
    ): UpdateTaskResponse

    @GET("tasks/{taskId}/users") // masih belum tau endpointnya
    suspend fun getTaskUsers(
        @Path("taskId") taskId: String,
    ): GetTaskUsersResponse // masih belum tau response nya

    // ======= MACHINE LEARNING =======
    @Headers("Content-Type: application/json")
    @POST("tasks/schedule/{projectId}")
    suspend fun scheduleML(
        @Path("projectId") projectId: String,
    ): ScheduleResponse

    @Headers("Content-Type: application/json")
    @POST("tasks/feedback/{projectId}")
    suspend fun feedbackML(
        @Path("projectId") projectId: String,
    ): FeedbackResponse
}
