import { Elm } from './Main.elm';

Elm.Main.init({
  node: document.getElementById('root'),
  flags: {
    graphQLEndpoint: process.env.ELM_APP_LONDO_GRAPH_QL_ENDPOINT,
    mainPageURL: process.env.ELM_APP_LONDO_MAIN_PAGE_URL,
    subFolders : {
      login: process.env.ELM_APP_LONDO_SUBFOLDER_LOGIN,
      register: process.env.ELM_APP_LONDO_SUBFOLDER_REGISTER
    }
  }
});