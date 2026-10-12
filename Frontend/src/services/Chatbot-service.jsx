import { privateAxios } from "./Helper"

export const askChatbot = async (message) => {
        const resp = await privateAxios.post(`/chat`, {message}, {responseType: 'text', timeout: 300000});
        return resp.data;
};