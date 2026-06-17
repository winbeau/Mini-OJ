CXX := g++
CXXFLAGS := -O2 -std=c++17
TARGET := judge
SRC := judge.cpp

.PHONY: all clean

all: $(TARGET)

$(TARGET): $(SRC)
	$(CXX) $(CXXFLAGS) -o $@ $<

clean:
	rm -f $(TARGET)
