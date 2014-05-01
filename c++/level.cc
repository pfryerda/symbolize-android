#include "level.h"
using namespace std;

Level::Level(Graph g, vector<Graph> s, unsigned int dr, unsigned int er):graph(g), solutions(s), drawRestirction(dr), eraseRestirction(er) {}
Level::~Level() {
	for (Graph::const_iterator i = graph.begin(); i != graph.end(); ++i) delete *i;
	for (vector<Graph>::const_iterator i = solutions.begin(); i != solutions.end(); ++i) {
		for (Graph::const_iterator j = i->begin(); j != i->end(); ++j) delete *j;
	}
}

const Graph Level::getGraph() const { return graph; }
const unsigned int Level::getDrawRestirction() const { return drawRestirction; }
const unsigned int Level::getEraseRestirction() const { return eraseRestirction; }

bool Level::checkSolution(Graph g) const {
	for (vector<Graph>::const_iterator s = solutions.begin(); s != solutions.end(); ++s) {
		if(s->size() == g.size()) {
			Graph soln = *s;
			Graph guess = g;
			while (soln.size() > 0) {
				vector<Graph::iterator> matches;
				for (Graph::iterator i = guess.begin(); i != guess.end(); ++i) {
					if (*(*i) == *soln.front()) matches.push_back(i);
				}
				if (matches.empty()) break;
				Graph::iterator candidate = matches[0];
				for(vector<Graph::iterator>::iterator i = matches.begin(); i != matches.end(); ++i) {
					if(soln.front()->score(*(*(*i))) < soln.front()->score(*(*candidate))) candidate = *i;
				}
				soln.pop_front();
				guess.erase(candidate);
			}
			if (soln.empty()) return true;
		}

	}
	return false;
}