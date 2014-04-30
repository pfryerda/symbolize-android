#ifndef __LEVEL_H__
#define __LEVEL_H__
#include "line.h"
#include <vector>

class Level {
	const Graph graph;
	const std::vector<Graph> solutions;
	const unsigned int drawRestirction;
	const unsigned int eraseRestirction;
public:
	Level(Graph, std::vector<Graph>, unsigned int, unsigned int);
	~Level();
	const Graph getGraph() const;
	const unsigned int getDrawRestirction() const;
	const unsigned int getEraseRestirction() const;
	//void printLevelCode(ostream&) const;

	bool checkSolution(Graph) const;
};

#endif