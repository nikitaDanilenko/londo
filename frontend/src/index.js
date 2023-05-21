import {Elm} from './Main.elm'
import './main.css'

const app = Elm.Main.init({
    node: document.getElementById('root'),
    flags: {
        graphQLEndpoint: process.env.ELM_APP_GRAPH_QL_ENDPOINT,
        mainPageURL: process.env.ELM_APP_MAIN_PAGE_URL
    }
});

const tokenKey = 'londo-user-token'

app.ports.storeToken.subscribe(function (token) {
    localStorage.setItem(tokenKey, token)
    app.ports.fetchToken.send(token)
})

app.ports.doFetchToken.subscribe(function () {
    const storedToken = localStorage.getItem(tokenKey)
    const tokenOrEmpty = storedToken ? storedToken : ''
    app.ports.fetchToken.send(tokenOrEmpty)
})

app.ports.doDeleteToken.subscribe(function () {
    localStorage.removeItem(tokenKey)
    app.ports.deleteToken.send(null)
})