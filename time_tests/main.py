import json

import numpy as np
from matplotlib import pyplot as plt

from sklearn.linear_model import LinearRegression


def main():
    results = {}

    for i in range(3, 18, 2):
        with open(f"/time_tests/result{i}.json") as f:
            results[i] = json.loads(f.readlines()[0])

    print(results)
    node_counts = list(map(lambda x: str(x), results.keys()))

    election_data = [[np.max(election) for election in results[int(count)]] for count in node_counts]
    election_data = np.array(election_data)

    # print(np.max(election_data))
    x_values_1 = np.concatenate([[3] * 100, [5] * 100, [7] * 100, [9] * 100, [11] * 100, [13] * 100, [15] * 100, [17] * 100]).ravel()
    y_values_1 = np.concatenate(election_data).ravel()
    # plt.scatter(x_values_1, y_values_1)

    x_values = node_counts
    y_values = np.mean(election_data, axis=1)


    trained_model = LinearRegression().fit(x_values_1.reshape((-1, 1)), y_values_1)
    y_pred = trained_model.predict(np.array([i for i in range(3,18,2)]).reshape((-1, 1)))

    print(np.mean(np.sqrt(np.square(np.array(y_values_1 - trained_model.predict(np.array(x_values_1).reshape((-1, 1))))))))

    plt.plot(x_values, y_values)
    plt.plot(x_values, y_pred, 'r')

    plt.scatter(list(map(lambda x: str(x), x_values_1)), y_values_1)

    plt.xlabel("Количество серверов в кластере, шт", size=12)
    plt.ylabel("Затраченное время на выбор лидера, мс", size=12)
    for index in range(len(x_values)):
        plt.text(index - 0.2, y_values[index] + 10, int(y_values[index]), size=12)

    plt.show()


if __name__ == '__main__':
    main()
