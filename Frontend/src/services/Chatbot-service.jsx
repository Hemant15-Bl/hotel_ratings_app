import { privateAxios } from "./Helper"

export const askChatbot = async (message) => {
        const resp = await privateAxios.post(`/chat`, {message}, {responseType: 'text', timeout: 120000});
        return resp.data;
};