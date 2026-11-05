import API_CONFIG from "../config/api.config";

const handleLogout = () => {
  localStorage.clear()
  window.location.href = "/login";
};

const refreshTokenApi = async (refreshToken) => {
  try {
    const response = await fetch(`${API_CONFIG.BASE_URL}/auth/refresh`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ refreshToken }),
    });
    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Failed to refresh token:", error);
    return null;
  }
};

const fetchWithConfig = async (
  endpoint,
  options = {},
  isRetry = false
) => {
  const url = `${API_CONFIG.BASE_URL}${endpoint}`;
  const accessToken = localStorage.getItem("access_token");
  const headers = {
    ...API_CONFIG.HEADERS,
    ...(accessToken && accessToken !== "undefined"
      ? { Authorization: `Bearer ${accessToken}` }
      : {}),
    ...(options.headers || {}),
  };
  const defaultOptions = { ...options, headers };

  try {
    const response = await fetch(url, defaultOptions);

    // Refresh only expired/invalid authentication; 403 means the user lacks a role.
    if (response.status === 401 && !isRetry) {
      const refreshToken = localStorage.getItem("refresh_token");

      if (refreshToken && endpoint !== "/auth/refresh") {
        try {
          const authResponse = await refreshTokenApi(refreshToken);
          if (authResponse) {
            localStorage.setItem("access_token", authResponse.accessToken);
            localStorage.setItem("refresh_token", authResponse.refreshToken);
            return fetchWithConfig(endpoint, options, true);
          } else {
            handleLogout();
            throw new Error("Session expired");
          }
        } catch (refreshError) {
          handleLogout();
          throw new Error("Session expired");
        }
      } else {
        throw new Error(`Request failed with status: ${response.status}`);
      }
    } else {
      if (response.status === 204) {
        return undefined;
      }
      const text = await response.text();
      if (!text) {
        return undefined;
      }
      return JSON.parse(text);
    }
  } catch (error) {
    console.error("API call failed:", error);
    throw error;
  }
};

export const ApiService = {
  register: async (firstName, lastName, phoneNumber, email, password) => {
    try {
      const response = await fetchWithConfig("/auth/register/parent", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          firstName,
          lastName,
          email,
          phoneNumber,
          password,
        }),
      });
      return response;
    } catch (error) {
      throw new Error("REGISTRATION_FAILED");
    }
  },

  changePassword: async (currentPassword, newPassword) => {

    try {
      const response = await fetchWithConfig("/auth/change-password", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          currentPassword,
          newPassword
        }),
      });
      return response;
    } catch (error) {
      throw new Error("PASSWORD_CHANGE_FAILED");
    }
  },

  getTrainerUpcomingTrainings: async (email) => {
    try {
      const response = await fetchWithConfig(
        `/trainer/${email}/training-unit/upcoming`,
        {
          method: "GET",
          headers: { "Content-Type": "application/json" },
        }
      );
      return response;
    } catch (error) {
      throw new Error("GETTING_UPCOMING_TRAININGS_FAILED");
    }
  },

  getTrainerPastTrainings: async (email) => {
    try {
      const response = await fetchWithConfig(
        `/trainer/${email}/training-unit/past`,
        {
          method: "GET",
          headers: { "Content-Type": "application/json" },
        }
      );
      return response;
    } catch (error) {
      throw new Error("GETTING_PAST_TRAININGS");
    }
  },

  updateTrainingUnitDescription: async (id, description) => {
    try {
      const response = await fetchWithConfig(
        `/training-unit/${id}/description`,
        {
          method: "PATCH",
          headers: {
            "Content-Type": "text/plain",
          },
          body: description,
        }
      );
      return response;
    } catch (error) {
      throw new Error("UPDATING_TRAINING_UNIT_FAILED");
    }
  },

  getTrainingUnitById: async (id) => {
    try {
      const response = await fetchWithConfig(`/training-unit/${id}`, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
      });
      return response;
    } catch (error) {
      throw new Error("GETTING_TRAINING_UNIT_FAILED");
    }
  },

  getTrainerAttendancesByTrainingUnitId: async (id) => {
    try {
      const response = await fetchWithConfig(
        `/training-unit/${id}/trainer-attendance`,
        {
          method: "GET",
          headers: { "Content-Type": "application/json" },
        }
      );
      return response;
    } catch (error) {
      throw new Error("GETTING_TRAINER_ATTENDANCES_FAILED");
    }
  },

  getChildAttendancesByTrainingUnitId: async (id) => {
    try {
      const response = await fetchWithConfig(
        `/training-unit/${id}/child-attendance`,
        {
          method: "GET",
          headers: { "Content-Type": "application/json" },
        }
      );
      return response;
    } catch (error) {
      throw new Error("GETTING_CHILD_ATTENDANCES_FAILED");
    }
  },

  markChildAttendancePresent: async (id) => {
    try {
      const response = await fetchWithConfig(
        `/child-attendance/${id}/mark-present`,
        {
          method: "PATCH",
          headers: { "Content-Type": "application/json" },
        }
      );
      return response;
    } catch (error) {
      throw new Error("MARK_CHILD_PRESENT_FAILED");
    }
  },

  markChildAttendanceAbsent: async (id) => {
    try {
      const response = await fetchWithConfig(
        `/child-attendance/${id}/mark-absent`,
        {
          method: "PATCH",
          headers: { "Content-Type": "application/json" },
        }
      );
      return response;
    } catch (error) {
      throw new Error("MARK_CHILD_ABSENT_FAILED");
    }
  },

  markTrainerAttendancePresent: async (id) => {
    try {
      const response = await fetchWithConfig(
        `/trainer-attendance/${id}/mark-present`,
        {
          method: "PATCH",
          headers: { "Content-Type": "application/json" },
        }
      );
      return response;
    } catch (error) {
      throw new Error("MARK_TRAINER_PRESENT_FAILED");
    }
  },

  markTrainerAttendanceAbsent: async (id) => {
    try {
      const response = await fetchWithConfig(
        `/trainer-attendance/${id}/mark-absent`,
        {
          method: "PATCH",
          headers: { "Content-Type": "application/json" },
        }
      );
      return response;
    } catch (error) {
      throw new Error("MARK_TRAINER_ABSENT_FAILED");
    }
  },

  getTrainerCurrentMonthReport: async (email) => {
    try {
      const response = await fetchWithConfig(
        `/trainer/${email}/report/current-month`,
        {
          method: "GET",
          headers: { "Content-Type": "application/json" },
        }
      );
      return response;
    } catch (error) {
      throw new Error("GET_TRAINER_TRAINING_REPORT_FAILED");
    }
  },

  getEvents: async () => {
    try {
      const response = await fetchWithConfig(`/event`, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
      });
      return response;
    } catch (error) {
      throw new Error("GET_EVENTS_FAILED");
    }
  },

  getEventRegisteredChildren: async (id) => {
    try {
      const response = await fetchWithConfig(
        `/event/${id}/registered-children`,
        {
          method: "GET",
          headers: { "Content-Type": "application/json" },
        }
      );
      return response;
    } catch (error) {
      throw new Error("GET_REGISTERED_CHILDREN_FOR_EVENT");
    }
  },

  toggleEventAttendance: async (id) => {
    try {
      const response = await fetchWithConfig(
        `/child-event-attendance/${id}/toggle-attendance`,
        {
          method: "PATCH",
          headers: { "Content-Type": "application/json" },
        }
      );
      return response;
    } catch (error) {
      throw new Error("TOGGLE_EVENT_ATTENDANCE");
    }
  },

  toggleChildPayment: async (id) => {
    try {
      const response = await fetchWithConfig(
        `/child-event-attendance/${id}/toggle-payment`,
        {
          method: "PATCH",
          headers: { "Content-Type": "application/json" },
        }
      );
      return response;
    } catch (error) {
      throw new Error("TOGGLE_CHILD_PAYMENT");
    }
  },

  getNews: async () => {
    try {
      const response = await fetchWithConfig(`/news`, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
      });
      return response;
    } catch (error) {
      throw new Error("GET_NEWS");
    }
  },

  getParentContact: async (id) => {
    try {
      const response = await fetchWithConfig(
        `/child-attendance/${id}/parent-contact`,
        {
          method: "GET",
          headers: { "Content-Type": "application/json" },
        }
      );
      return response;
    } catch (error) {
      throw new Error("GET_PARENT_CONTACT");
    }
  },

  /******************* PARENT ********************/

  getParentUpcomingTrainings: async (email) => {
    try {
      const response = await fetchWithConfig(
        `/parent/${email}/training-unit/upcoming`,
        {
          method: "GET",
          headers: { "Content-Type": "application/json" },
        }
      );
      return response;
    } catch (error) {
      throw new Error("GET_PARENT_UPCOMING_TRAININGS");
    }
  },

  getParentChildren: async (email) => {
    try {
      const response = await fetchWithConfig(`/parent/${email}/children`, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
      });
      return response;
    } catch (error) {
      throw new Error("GET_PARENT_UPCOMING_TRAININGS");
    }
  },

  getSchools: async () => {
    try {
      const response = await fetchWithConfig(`/school`, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
      });
      return response;
    } catch (error) {
      throw new Error("GET_SCHOOLS");
    }
  },

  getTrainingsBySchoolId: async (id) => {
    try {
      const response = await fetchWithConfig(`/school/${id}/trainings`, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
      });
      return response;
    } catch (error) {
      throw new Error("GET_TRAININGS_OF_SCHOOL");
    }
  },

  createChild: async (email, childDto) => {
    try {
      const response = await fetchWithConfig(`/parent/${email}/create-child`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(childDto),
      });
      return response;
    } catch (error) {
      throw new Error("CREATE_CHILD");
    }
  },

  getChildrenEventStatus: async (email, eventId) => {
    try {
      const response = await fetchWithConfig(
        `/parent/${email}/children/event-status/${eventId}`,
        {
          method: "GET",
          headers: { "Content-Type": "application/json" },
        }
      );
      return response;
    } catch (error) {
      throw new Error("GET_CHILDREN_EVENT_STATUS");
    }
  },

  registerChildToEvent: async (childId, eventId, note) => {
    try {
      const response = await fetchWithConfig(
        `/event/${eventId}/register-child/${childId}`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(note),
        }
      );
      return response;
    } catch (error) {
      throw new Error("REGISTER_CHILD_TO_EVENT");
    }
  },

  getChildAttencanceDetail: async (childId) => {
    try {
      const response = await fetchWithConfig(
        `/child/${childId}/attendance-detail`,
        {
          method: "GET",
          headers: { "Content-Type": "application/json" },
        }
      );
      return response;
    } catch (error) {
      throw new Error("GET_CHILD_ATTENDANCE_DETAIL");
    }
  },

  /******************* ADMIN ********************/

  createSchool: async (schoolDto) => {
    try {
      const response = await fetchWithConfig(`/school/create`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(schoolDto),
      });
      return response;
    } catch (error) {
      throw new Error("CREATE_SCHOOL");
    }
  },

  createTraining: async (schoolId, trainingDto) => {
    try {
      const response = await fetchWithConfig(`/training/create/${schoolId}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(trainingDto),
      });
      return response;
    } catch (error) {
      throw new Error("CREATE_TRAINING");
    }
  },

  getAllTrainers: async () => {
    try {
      const response = await fetchWithConfig(`/trainer`, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
      });
      return response;
    } catch (error) {
      throw new Error("GET_TRAINERS");
    }
  },
  
  getTraningUpcomingDates: async (id) => {
    try {
      const response = await fetchWithConfig(`/training/${id}/upcoming-dates`, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
      });
      return response;
    } catch (error) {
      throw new Error("GET_TRAINING_UPCOMING_DATES");
    }
  },

  getTrainersByTrainingId: async (id) => {
    try {
      const response = await fetchWithConfig(`/training/${id}/trainers`, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
      });
      return response;
    } catch (error) {
      throw new Error("GET_TRAINERS_OF_TRAINING");
    }
  },

  createTrainingSubstitution: async (trainingId, substitutionDto) => {
    try {
      const response = await fetchWithConfig(
        `/training/${trainingId}/create-trainer-substitution`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(substitutionDto),
        }
      );
      return response;
    } catch (error) {
      throw new Error("CREATE_TRAINING");
    }
  },

  getUnassignedChildren: async () => {
    try {
      const response = await fetchWithConfig(`/child/unassigned-children`, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
      });
      return response;
    } catch (error) {
      throw new Error("GET_UNASSIGNED_CHILDREN");
    }
  },

  addChildToTraining: async (id) => {
    try {
      const response = await fetchWithConfig(`/child/${id}/add-to-training`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
      });
      return response;
    } catch (error) {
      throw new Error("ADD_CHILD_TO_TRAINING");
    }
  },

  deleteChild: async (id) => {
    try {
      const response = await fetchWithConfig(`/child/${id}/delete`, {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
      });
} catch (error) {
      throw new Error("DELETE_CHILD");
    }
  },

  updateChildInfo: async (id, childDto) => {
    try {
      const response = await fetchWithConfig(`/child/${id}/update`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(childDto),
      });
      return response;
    } catch (error) {
      throw new Error("EDIT_CHILD_INFO");
    }
  },

  updateParentInfo: async (id, parentDto) => {
    try {
      const response = await fetchWithConfig(`/child/${id}/update-parent`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(parentDto),
      });
      return response;
    } catch (error) {
      throw new Error("EDIT_PARENT_INFO");
    }
  },

  createNews: async (newsDto) => {
    try {
      const response = await fetchWithConfig(`/news/create`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(newsDto),
      });
      return response;
    } catch (error) {
      throw new Error("CREATE_NEWS");
    }
  },

  createEvent: async (eventDto) => {
    try {
      const response = await fetchWithConfig(`/event/create`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(eventDto),
      });
      return response;
    } catch (error) {
      throw new Error("CREATE_EVENT");
    }
  },

  addTrainerToEvent: async (eventId, trainerId) => {
    try {
      const response = await fetchWithConfig(
        `/event/${eventId}/add-trainer/${trainerId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      return response;
    } catch (error) {
      throw new Error("ADD_TRAINER_TO_EVENT");
    }
  },

  addTrainerToTraining: async (trainingId, trainerId) => {
  try {
    const response = await fetchWithConfig(
      `/training/${trainingId}/add-trainer/${trainerId}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
      }
    );
      return response;
  } catch (error) {
    throw new Error("ADD_TRAINER_TO_TRAINING");
  }
},

  removeTrainerFromEvent: async (eventId, trainerId) => {
    try {
      const response = await fetchWithConfig(
        `/event/${eventId}/remove-trainer/${trainerId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      return response;
    } catch (error) {
      throw new Error("REMOVE_TRAINER_FROM_EVENT");
    }
  },

  getEventRegisteredTrainers: async (id) => {
    try {
      const response = await fetchWithConfig(
        `/event/${id}/registered-trainers`,
        {
          method: "GET",
          headers: { "Content-Type": "application/json" },
        }
      );
      return response;
    } catch (error) {
      throw new Error("GET_REGISTERED_TRAINERS");
    }
  },

  getHolidaysBySchoolId: async (id) => {
    try {
      const response = await fetchWithConfig(
        `/school/${id}/holidays`,
        {
          method: "GET",
          headers: { "Content-Type": "application/json" },
        }
      );
      return response;
    } catch (error) {
      throw new Error("GET_HOLIDAYS_BY_SCHOOL");
    }
  },

  addHolidayToSchool: async (schoolId, holidayDto) => {
    try {
      const response = await fetchWithConfig(
        `/school/${schoolId}/add-holiday`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(holidayDto),
        }
      );
      return response;
    } catch (error) {
      throw new Error("ADD_HOLIDAY_TO_SCHOOL");
    }
  },

  addHolidayToAllSchools: async (holidayDto) => {
    try {
      const response = await fetchWithConfig(
        `/school/add-holiday`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(holidayDto),
        }
      );
      return response;
    } catch (error) {
      throw new Error("ADD_HOLIDAY_ALL_SCHOOLS");
    }
  },

  registerTrainer: async (trainerRequest) => {
    try {
      const response = await fetchWithConfig(
        `/auth/register/trainer`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(trainerRequest),
        }
      );
      return response;
    } catch (error) {
      throw new Error("REGISTER_TRAINER");
    }
  },

  timeMachine: async () => {
    try {
      const response = await fetchWithConfig(
        `/training-unit/create-weekly-training-units`,
        {
          method: "GET",
          headers: { "Content-Type": "application/json" }
        }
      );
      return response;
    } catch (error) {
      throw new Error("CREATE_WEEKLY_TRAININGS");
    }
  },

};
