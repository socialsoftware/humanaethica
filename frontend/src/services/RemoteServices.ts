import axios from 'axios';
import Store from '@/store';
import TokenAuthUser from '@/models/user/TokenAuthUser';
import RegisterUser from '@/models/user/RegisterUser';
import router from '@/router';
import User from '@/models/user/User';
import Institution from '@/models/institution/Institution';
import RegisterInstitution from '@/models/institution/RegisterInstitution';
import RegisterVolunteer from '@/models/volunteer/RegisterVolunteer';
import RegisterMember from '@/models/member/RegisterMember';
import AuthPasswordDto from '@/models/user/AuthPasswordDto';
import Theme from '@/models/theme/Theme';
import RegisterTheme from '@/models/theme/RegisterTheme';

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
  (error) => Promise.reject(error)
);
httpClient.interceptors.response.use(
  (response) => {
    if (response.data.notification) {
      if (response.data.notification.errorMessages.length)
        Store.dispatch(
          'notification',
          response.data.notification.errorMessages
        );
      response.data = response.data.response;
    }
    return response;
  },
  (error) => Promise.reject(error)
);

export default class RemoteServices {
  // AuthUser Controller

  static async userLogin(
    username: string,
    password: string
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
        }
      )
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

  static async registerMember(
    member: RegisterMember,
    doc: File,
    institutionId: number
  ) {
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
        }
      )
    );
    return httpClient
      .post(`/users/${institutionId}/registerMember`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async confirmRegistration(
    registerUser: RegisterUser
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
    memberDoc: File
  ) {
    const formData = new FormData();
    formData.append('institutionDoc', institutionDoc);
    formData.append('memberDoc', memberDoc);
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
        }
      )
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

  static async deleteInstitution(
    institutionId: number
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

  static async validateInstitution(institutionId: number) {
    return httpClient
      .post(`/institution/${institutionId}/validate`)
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

  // Theme Controler

    static async registerTheme(theme: Theme) {
        return httpClient
            .post('/themes/register', theme)
            .then(() => {
                return;
            })
            .catch(async (error) => {
                throw Error(await this.errorMessage(error));
            });
    }

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
