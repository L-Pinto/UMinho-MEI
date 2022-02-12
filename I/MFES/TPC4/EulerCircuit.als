sig Nodo {
    adj : set Nodo,             // arestas
    var conhecidos  : set Nodo  // conjunto dos visitados
}
var one sig Inicial, Atual in Nodo {} 

// Condições iniciais do sistema
fact Init {
    // Ainda não se fez a travessia do grafo
    no conhecidos 

    // Um nodo inicial e simultaneamente atual.
    one n : Nodo | Inicial = n && Atual = n

    // GRAFO : bidirecional
    adj = ~adj

    // GRAFO : sem lacetes
    no ( iden & adj )

    // GRAFO : completo/ligado
    all n :Nodo | Nodo-n in n.adj
}
    
// Percorrer o grafo
pred travessia [n : Nodo] {
    // guard
    (n in Atual.adj) && (n not in Atual.conhecidos)
     
    //effect   
    conhecidos' = conhecidos + Atual->n + n->Atual // grafo é bi-direcional
    Atual' = n // next node

    //frame conditions
    Inicial' = Inicial
}

//convergiu
pred nop {
    //guarda
    conhecidos = adj // percorreu todo o grafo
    Atual = Inicial

    // frame conditions
    Inicial' = Inicial // sinalizador de inicio e fim 
    Atual' = Atual
    conhecidos' = conhecidos
}


fact Traces { 
    always (nop or 
            some n : Nodo | travessia[n] )
}


// Especifique um cenário run com um grafo completo de 5 nodos
run Exemplo{

} for exactly 5 Nodo, 20 steps