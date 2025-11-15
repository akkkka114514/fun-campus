import { defineStore } from 'pinia';

export const useAppStore = defineStore('appStore', {
	state: () => {
		return {
			app_name: '',
			app_logo: '',
			app_carousel:[]
		};
	},
	actions: {
		setAppInfo({ base } : any) {
			this.app_name = base.app_name;
			this.app_logo = base.logo;
			this.app_carousel = base.app_carousel || [];
		},
	},
});