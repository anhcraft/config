class TrieNode {
    constructor() {
        this.children = {};
        this.keywords = new Set();
    }
}

class Trie {
    constructor() {
        this.root = new TrieNode();
    }

    insert(keyword) {
        let node = this.root;
        for (let char of keyword) {
            if (!node.children[char])
                node.children[char] = new TrieNode();
            node = node.children[char];
            node.keywords.add(keyword);
        }
        node.eow = true;
    }

    search(prefix) {
        let node = this.root;
        for (let char of prefix) {
            if (!node.children[char])
                return [];
            node = node.children[char];
        }
        return Array.from(node.keywords);
    }
}

const normalize = (term) => term.toLowerCase().trim();

const buildTrie = (keywords) => {
    const trie = new Trie();
    for (let keyword of keywords)
        trie.insert(normalize(keyword));
    return trie;
};

/*
Schema index: <schema index> <schema name>
    0: schema1{name, link}
    1: schema2{name, link}
    2: schema3{name, link}
    n: schemaN+1{name, link}

Keyword index: <keyword>: <list of schema indexes>
    keyword1: 0, 1
    keyword2: 1, 2, 3
 */

const schemaIndex = [/*__SCHEMA_INDEX__*/];
const keywordIndex = {/*__KEYWORD_INDEX__*/};

const trie = buildTrie(Object.keys(keywordIndex));

const search = (searchTerm) => {
    const schemas = []
    const matchedSchemas = new Set();
    const matchedKeywords = trie.search(normalize(searchTerm));
    for (let keyword of matchedKeywords) {
        if (!keywordIndex[keyword])
            continue;
        for (let schema of keywordIndex[keyword]){
            if (matchedSchemas.has(schema))
                continue;
            schemas.push(schemaIndex[schema]);
            matchedSchemas.add(schema);
        }
    }
    return {
        schemas: schemas,
        keywords: matchedKeywords
    };
};
