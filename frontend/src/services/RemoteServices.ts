import axios from 'axios';
import Store from '@/store';
import TokenAuthUser from '@/models/user/TokenAuthUser';
import RegisterUser from '@/models/user/RegisterUser';
import router from '@/router';
import User from '@/models/user/User';
import Institution from '@/models/institution/Institution';
import Activity from '@/models/activity/Activity';
import RegisterInstitution from '@/models/institution/RegisterInstitution';
import RegisterVolunteer from '@/models/volunteer/RegisterVolunteer';
import RegisterMember from '@/models/member/RegisterMember';
import AuthPasswordDto from '@/models/user/AuthPasswordDto';
import Theme from '@/models/theme/Theme';
import Enrollment from '@/models/enrollment/Enrollment';
import Participation from '@/models/participation/Participation';
import Assessment from '@/models/assessment/Assessment';
import Report from '@/models/report/Report';

const httpClient = axios.create();
httpClient.defaults.timeout = 100000;
httpClient.defaults.baseURL =
  process.env.VUE_APP_ROOT_API || 'http://localhost:8080';
httpClient.defaults.headers.post['Content-Type'] = 'application/json';
httpClient.interceptors.request.use(
  (config) => {
    if (config.headers !== undefined && !config.headers.Authorization) {
      const token = Store.getters.getToken;

      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
    return config;
  },
  (error) => Promise.reject(error),
);
httpClient.interceptors.response.use(
  (response) => {
    if (response.data != null && response.data.notification) {
      if (response.data.notification.errorMessages.length)
        Store.dispatch(
          'notification',
          response.data.notification.errorMessages,
        );
      response.data = response.data.response;
    }
    return response;
  },
  (error) => Promise.reject(error),
);

export default class RemoteServices {
  // AuthUser Controller

  static async userLogin(
    username: string,
    password: string,
  ): Promise<TokenAuthUser> {
    return httpClient
      .post('/auth/user', new AuthPasswordDto(username, password))
      .then((response) => {
        return new TokenAuthUser(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async demoVolunteerLogin(): Promise<TokenAuthUser> {
    return httpClient
      .get('/auth/demo/volunteer')
      .then((response) => {
        return new TokenAuthUser(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async demoMemberLogin(): Promise<TokenAuthUser> {
    return httpClient
      .get('/auth/demo/member')
      .then((response) => {
        return new TokenAuthUser(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // User Controller

  static async getUsers(): Promise<User[]> {
    return httpClient
      .get('/users')
      .then((response) => {
        return response.data.map((user: any) => {
          return new User(user);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteUser(userId: number): Promise<User[]> {
    return httpClient
      .delete(`/users/${userId}`)
      .then((response) => {
        return response.data.map((user: any) => {
          return new User(user);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async validateUser(userId: number) {
    return httpClient.post(`/users/${userId}/validate`).catch(async (error) => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async registerUser(user: User): Promise<User> {
    return httpClient
      .post('/users/register', user)
      .then((response) => {
        return new User(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async registerVolunteer(volunteer: RegisterVolunteer, doc: File) {
    const formData = new FormData();
    formData.append('file', doc);
    formData.append(
      'volunteer',
      new Blob(
        [
          JSON.stringify({
            name: volunteer.volunteerName,
            username: volunteer.volunteerUsername,
            email: volunteer.volunteerEmail,
          }),
        ],
        {
          type: 'application/json',
        },
      ),
    );
    return httpClient
      .post('/users/registerVolunteer', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async registerMember(member: RegisterMember, doc: File) {
    const formData = new FormData();
    formData.append('file', doc);
    formData.append(
      'member',
      new Blob(
        [
          JSON.stringify({
            name: member.memberName,
            username: member.memberUsername,
            email: member.memberEmail,
          }),
        ],
        {
          type: 'application/json',
        },
      ),
    );
    return httpClient
      .post('/users/registerInstitutionMember', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getThemesbyInstitution(): Promise<Theme[]> {
    return httpClient
      .get('/theme/getInstitutionThemes')
      .then((response) => {
        return response.data.map((theme: any) => {
          return new Theme(theme);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getThemesAvailableforInstitution(): Promise<Theme[]> {
    return httpClient
      .get('/theme/availableThemesforInstitution')
      .then((response) => {
        return response.data.map((theme: any) => {
          return new Theme(theme);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async addThemetoInstitution(theme: Theme) {
    return httpClient
      .put(`/theme/${theme.id}/addInstitution`)
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async removeThemetoInstitution(themeId: number) {
    return httpClient
      .put(`/theme/${themeId}/removeInstitution`)
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async confirmRegistration(
    registerUser: RegisterUser,
  ): Promise<RegisterUser> {
    return httpClient
      .post('/users/register/confirm', registerUser)
      .then((response) => {
        return new RegisterUser(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getUserDocument(userId: number) {
    return httpClient
      .get(`/user/${userId}/document`, {
        responseType: 'blob',
      })
      .then((response) => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', 'Document.pdf');
        document.body.appendChild(link);
        link.click();
        if (link.parentNode != null) {
          link.parentNode.removeChild(link);
        }
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getForm() {
    return httpClient
      .get('/document/form', {
        responseType: 'blob',
      })
      .then((response) => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', 'Form.pdf');
        document.body.appendChild(link);
        link.click();
        if (link.parentNode != null) {
          link.parentNode.removeChild(link);
        }
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // Institution Controller

  static async registerInstitution(
    institution: RegisterInstitution,
    institutionDoc: File,
    memberDoc: File,
  ) {
    const formData = new FormData();
    formData.append('institutionDocument', institutionDoc);
    formData.append('memberDocument', memberDoc);
    formData.append(
      'institution',
      new Blob(
        [
          JSON.stringify({
            institutionEmail: institution.institutionEmail,
            institutionName: institution.institutionName,
            institutionNif: institution.institutionNif,
            memberEmail: institution.memberEmail,
            memberUsername: institution.memberUsername,
            memberName: institution.memberName,
          }),
        ],
        {
          type: 'application/json',
        },
      ),
    );
    return httpClient
      .post('/institution/register', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getInstitutions(): Promise<Institution[]> {
    return httpClient
      .get('/institutions')
      .then((response) => {
        return response.data.map((institution: any) => {
          return new Institution(institution);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getInstitution(userId: number): Promise<Institution> {
    return httpClient
      .get(`/users/${userId}/getInstitution`)
      .then((response) => {
        return new Institution(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteInstitution(
    institutionId: number,
  ): Promise<Institution[]> {
    return httpClient
      .delete(`/institutions/${institutionId}`)
      .then((response) => {
        return response.data.map((institution: any) => {
          return new Institution(institution);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async validateInstitution(
    institutionId: number,
  ): Promise<Institution[]> {
    return httpClient
      .post(`/institution/${institutionId}/validate`)
      .then((response) => {
        return response.data.map((institution: any) => {
          return new Institution(institution);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getInstitutionDocument(institutionId: number) {
    return httpClient
      .get(`/institution/${institutionId}/document`, {
        responseType: 'blob',
      })
      .then((response) => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', 'Document.pdf');
        document.body.appendChild(link);
        link.click();
        if (link.parentNode != null) {
          link.parentNode.removeChild(link);
        }
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async registerActivity(activity: Activity) {
    return httpClient
      .post('/activities', activity)
      .then((response) => {
        return new Activity(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getActivities(): Promise<Activity[]> {
    return httpClient
      .get('/activities')
      .then((response) => {
        return response.data.map((activity: any) => {
          return new Activity(activity);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateActivityAsMember(activityId: number, activity: Activity) {
    return httpClient
      .put(`/activities/${activityId}`, activity)
      .then((response) => {
        return new Activity(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async validateActivity(activityId: number) {
    return httpClient
      .put(`/activities/${activityId}/validate`)
      .then((response) => {
        return new Activity(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async suspendActivity(activityId: number, justification: string) {
    return httpClient
      .put(`/activities/${activityId}/suspend/${justification}`)
      .then((response) => {
        return new Activity(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async reportActivity(userId: number, activityId: number) {
    return httpClient
      .put(`/activities/${activityId}/report`)
      .then((response) => {
        return new Activity(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // Enrollment controller

  static async getActivityEnrollments(activityId: number) {
    return httpClient
      .get(`/activities/${activityId}/enrollments`)
      .then((response) => {
        return response.data.map((enrollment: any) => {
          return new Enrollment(enrollment);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getVolunteerEnrollments(): Promise<Enrollment[]> {
    return httpClient
      .get('/enrollments/volunteer')
      .then((response) => {
        return response.data.map((enrollment: any) => {
          return new Enrollment(enrollment);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createEnrollment(activityId: number, enrollment: Enrollment) {
    return httpClient
      .post(`/activities/${activityId}/enrollments`, enrollment)
      .then((response) => {
        return new Enrollment(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async editEnrollment(enrollmentId: number, enrollment: Enrollment) {
    return httpClient
      .put(`/enrollments/${enrollmentId}`, enrollment)
      .then((response) => {
        return new Enrollment(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async removeEnrollment(enrollmentId: number) {
    return httpClient
      .delete(`/enrollments/${enrollmentId}`)
      .then((response) => {
        return new Enrollment(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // Participation Controller

  static async getVolunteerParticipations(): Promise<Participation[]> {
    return httpClient
      .get('/participations/volunteer')
      .then((response) => {
        return response.data.map((participation: any) => {
          return new Participation(participation);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getActivityParticipations(
    activityId: number | null,
  ): Promise<Participation[]> {
    return httpClient
      .get(`/activities/${activityId}/participations`)
      .then((response) => {
        return response.data.map((participation: any) => {
          return new Participation(participation);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createParticipation(
    activityId: number,
    participation: Participation,
  ) {
    return httpClient
      .post(`/activities/${activityId}/participations`, participation)
      .then((response) => {
        return new Participation(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteParticipation(participationId: number) {
    return httpClient
      .delete(`participations/${participationId}`)
      .then((response) => {
        return new Participation(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateParticipationMember(
    participationId: number,
    participation: Participation,
  ) {
    return httpClient
      .put(`/participations/${participationId}/member`, participation)
      .then((response) => {
        return new Participation(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateParticipationVolunteer(
    participationId: number,
    participation: Participation,
  ) {
    return httpClient
      .put(`/participations/${participationId}/volunteer`, participation)
      .then((response) => {
        return new Participation(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // Assessment Controller

  static async getVolunteerAssessments(): Promise<Assessment[]> {
    return httpClient
      .get('/assessments/volunteer')
      .then((response) => {
        return response.data.map((assessment: any) => {
          return new Assessment(assessment);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getInstitutionAssessments(
    institutionId: number | null,
  ): Promise<Assessment[]> {
    return httpClient
      .get(`/institutions/${institutionId}/assessments`)
      .then((response) => {
        return response.data.map((assessment: any) => {
          return new Assessment(assessment);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createAssessment(institutionId: number, assessment: Assessment) {
    return httpClient
      .post(`/institutions/${institutionId}/assessments`, assessment)
      .then((response) => {
        return new Assessment(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateAssessment(assessment: Assessment) {
    return httpClient
      .put(`/assessments/${assessment.id}`, assessment)
      .then((response) => {
        return new Assessment(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteAssessment(assessmentId: number): Promise<Assessment> {
    return httpClient
      .delete(`/assessments/${assessmentId}`)
      .then((response) => new Assessment(response.data))
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // Report Controller

  static async getActivityReports(activityId: number) {
    return httpClient
      .get(`/activities/${activityId}/reports`)
      .then((response) => {
        return response.data.map((report: any) => {
          return new Report(report);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getVolunteerReports(volunteerId: number) {
    return httpClient
      .get(`/volunteers/${volunteerId}/reports`)
      .then((response) => {
        return response.data.map((report: any) => {
          return new Report(report);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createReport(activityId: number, report: Report) {
    return httpClient
      .post(`/activities/${activityId}/reports`, report)
      .then((response) => {
        return new Report(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteReport(reportId: number) {
    return httpClient
      .delete(`/reports/${reportId}`)
      .then((response) => {
        return new Report(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getVolunteerReportsAsVolunteer(): Promise<Report[]> {
    return httpClient
      .get('/reports/volunteer')
      .then((response) => {
        return response.data.map((report: any) => {
          return new Report(report);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // Theme Controller

  static async getThemes(): Promise<Theme[]> {
    return httpClient
      .get('/themes')
      .then((response) => {
        return response.data.map((theme: any) => {
          return new Theme(theme);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getThemesAvailable(): Promise<Theme[]> {
    return httpClient
      .get('/themes/availableThemes')
      .then((response) => {
        return response.data.map((theme: any) => {
          return new Theme(theme);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async registerTheme(theme: Theme): Promise<Theme> {
    return httpClient
      .post('/themes/register', theme)
      .then((response) => {
        return new Theme(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async registerThemeInstitution(theme: Theme) {
    return httpClient
      .post('/themes/registerInstitution', theme)
      .then((response) => {
        return new Theme(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async validateTheme(themeId: number) {
    return httpClient
      .put(`/themes/${themeId}/validate`)
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteTheme(themeId: number): Promise<Theme[]> {
    return httpClient
      .delete(`/themes/${themeId}/delete`)
      .then((response) => {
        return response.data.map((theme: any) => {
          return new Theme(theme);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // Error

  static async errorMessage(error: any): Promise<string> {
    if (error.message === 'Network Error') {
      return 'Unable to connect to server';
    } else if (error.message === 'Request failed with status code 403') {
      await Store.dispatch('logout');
      await router.push({ path: '/' });
      return 'Unauthorized access or expired token';
    } else if (error.message.split(' ')[0] === 'timeout') {
      return 'Request timeout - Server took too long to respond';
    } else if (error.response) {
      return error.response.data.message;
    } else {
      console.log(error);
      return 'Unknown Error - Contact admin';
    }
  }
}
