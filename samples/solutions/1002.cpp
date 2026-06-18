#include <algorithm>
#include <iostream>

int main() {
    long long a;
    long long b;
    long long c;
    if (std::cin >> a >> b >> c) {
        std::cout << std::max(a, std::max(b, c)) << '\n';
    }
    return 0;
}
