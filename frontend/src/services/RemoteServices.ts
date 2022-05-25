import axios from 'axios';
import Store from '@/store';
import TokenAuthUser from '@/models/user/TokenAuthUser';
import RegisterUser from '@/models/user/RegisterUser';
import router from '@/router';
import User from '@/models/user/User';

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
      .get(`/auth/user?username=${username}&password=${password}`)
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

  static async deleteUser(userId: number) {
    return httpClient.delete(`/users/${userId}`).catch(async (error) => {
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
