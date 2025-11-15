import { defineStore } from 'pinia';

export const useActivityStore = defineStore('activityStore', {
	state: () => {
		return {
			fieldList:[]
		};
	},
	actions: {
		setFieldList(list:any) {
            this.fieldList = list;
		},
	},
});