package com.davidpe.tasker.application.ui.stats;

import com.davidpe.tasker.application.service.task.TaskService;
import com.davidpe.tasker.application.service.user.UserService;
import com.davidpe.tasker.application.stats.GetTaskMetricTimeSeriesDatasetRequest;
import com.davidpe.tasker.application.stats.GetTaskMetricTimeSeriesDatasetUseCase;
import com.davidpe.tasker.application.ui.common.UiControllerDataAware;
import com.davidpe.tasker.application.ui.common.UiScreenController;
import com.davidpe.tasker.application.ui.common.UiScreenId;
import com.davidpe.tasker.application.ui.controls.Chart2DController;
import com.davidpe.tasker.application.ui.controls.BreadcrumbBar;
import com.davidpe.tasker.application.ui.controls.BreadcrumbBar.BreadcrumbItem;
import com.davidpe.tasker.application.ui.events.WindowClosedEvent;
import com.davidpe.tasker.domain.project.Project;
import com.davidpe.tasker.domain.stats.StatsAggregationLevel;
import com.davidpe.tasker.domain.stats.TaskMetric;
import com.davidpe.tasker.domain.stats.TaskStatsQuery;
import com.davidpe.tasker.domain.stats.TimeSeries;
import com.davidpe.tasker.domain.stats.TimeSeriesDataset;
import com.davidpe.tasker.domain.stats.TimeSeriesPoint;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class StatsSceneController extends UiScreenController
    implements UiControllerDataAware<StatsSceneData> {

  @FXML private Button btnFilter;

  @FXML private ComboBox<StatsAggregationLevel> cbxGroupBy;

  @FXML private ComboBox<Project> cbxProject;

  @FXML private DatePicker dpEndDate;

  @FXML private DatePicker dpStartDate;

  @FXML private Chart2DController grhStats;

  @FXML private BreadcrumbBar breadcrumbBar;

  private final TaskService taskService;
  private final UserService userService;
  private final GetTaskMetricTimeSeriesDatasetUseCase getTaskMetricTimeSeriesDatasetUseCase;

  private ApplicationEventPublisher eventPublisher;

  @Lazy
  public StatsSceneController(
      ApplicationEventPublisher eventPublisher,
      TaskService taskService,
      UserService userService,
      GetTaskMetricTimeSeriesDatasetUseCase getTaskMetricTimeSeriesDatasetUseCase) {

    this.eventPublisher = eventPublisher;
    this.taskService = taskService;
    this.userService = userService;
    this.getTaskMetricTimeSeriesDatasetUseCase = getTaskMetricTimeSeriesDatasetUseCase;
  }

  @FXML
  void buttonAction(ActionEvent event) {

    if (isButtonFilterClicked(event)) {
      refreshChart();
    }
  }

  @FXML
  void backToMain() {
    eventPublisher.publishEvent(new WindowClosedEvent(UiScreenId.STATS));
  }

  @FXML
  void onGroupByChanged(ActionEvent event) {
    refreshChart();
  }

  @FXML
  void onProjectChanged(ActionEvent event) {
    refreshChart();
  }

  private boolean isButtonFilterClicked(ActionEvent event) {

    return event.getSource() == btnFilter;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    breadcrumbBar.setItems(
        BreadcrumbItem.link("Tasker", this::backToMain),
        BreadcrumbItem.current("Statistics"),
        BreadcrumbItem.current("Overview"));
    cbxGroupBy.setItems(FXCollections.observableArrayList(StatsAggregationLevel.values()));
    cbxGroupBy.setConverter(
        new StringConverter<>() {
          @Override
          public String toString(StatsAggregationLevel level) {
            if (level == null) {
              return "";
            }
            return switch (level) {
              case DAILY -> "Daily";
              case WEEKLY -> "Weekly";
              case MONTHLY -> "Monthly";
            };
          }

          @Override
          public StatsAggregationLevel fromString(String string) {
            return StatsAggregationLevel.valueOf(string.toUpperCase());
          }
        });

    cbxProject
        .getItems()
        .setAll(taskService.getProjectsByUserId(userService.getSelectedUser().getId()));
    if (!cbxProject.getItems().isEmpty()) {
      cbxProject.getSelectionModel().selectFirst();
    }
    cbxGroupBy.getSelectionModel().select(StatsAggregationLevel.MONTHLY);

    dpEndDate.setValue(LocalDate.now());
    dpStartDate.setValue(LocalDate.now().minusMonths(1));

    refreshChart();
  }

  @Override
  public void resetData() {
    refreshChart();
  }

  @Override
  public void setData(StatsSceneData data) {
    // Appearance is resolved by ApplicationThemeService at the screen root.
  }

  @Override
  public StatsSceneData getData() {

    return new StatsSceneData(Boolean.TRUE);
  }

  private void refreshChart() {
    Project project = cbxProject.getSelectionModel().getSelectedItem();
    StatsAggregationLevel aggregationLevel = cbxGroupBy.getSelectionModel().getSelectedItem();
    LocalDate fromDate = dpStartDate.getValue();
    LocalDate toDate = dpEndDate.getValue();

    if (project == null || aggregationLevel == null || fromDate == null || toDate == null) {
      grhStats.resetDataset();
      return;
    }

    TaskStatsQuery query = new TaskStatsQuery(project.getId(), fromDate, toDate, aggregationLevel);
    GetTaskMetricTimeSeriesDatasetRequest request =
        new GetTaskMetricTimeSeriesDatasetRequest(TaskMetric.CREATED_TASKS, query);

    TimeSeriesDataset dataset = getTaskMetricTimeSeriesDatasetUseCase.execute(request);

    ChartData chartData = convertToChartData(dataset);
    System.out.println("ChartData: " + chartData.dataPoints().size() + " series");
    if (chartData.dataPoints().isEmpty() || chartData.labels().isEmpty()) {
      grhStats.resetDataset();
      grhStats.setChartTitle("Tasks created for " + project.getName());
      return;
    }

    chartData.dataPoints().getFirst().forEach(point -> System.out.println("DataPoint: " + point));
    System.out.println("ChartLabels: " + chartData.labels());

    grhStats.setChartTitle("Tasks created for " + project.getName());
    grhStats.setSeriesNames(chartData.seriesNames);
    grhStats.setDatasets(chartData.dataPoints, chartData.labels);
  }

  private ChartData convertToChartData(TimeSeriesDataset dataset) {
    if (dataset == null || dataset.isEmpty()) {
      return new ChartData(List.of(), List.of(), List.of());
    }

    List<TimeSeries> seriesList = dataset.getSeries();
    List<String> seriesNames = new ArrayList<>();
    List<List<Chart2DController.DataPoint2D>> allDataPoints = new ArrayList<>();

    Set<String> allLabelsSet = new LinkedHashSet<>();
    Map<String, Map<String, Double>> seriesDataByLabel = new LinkedHashMap<>();

    for (TimeSeries series : seriesList) {
      String seriesName = getSeriesDisplayName(series.getName());
      seriesNames.add(seriesName);

      for (TimeSeriesPoint point : series.getPoints()) {
        String label = point.getPeriod().getLabel();
        allLabelsSet.add(label);

        seriesDataByLabel
            .computeIfAbsent(seriesName, key -> new LinkedHashMap<>())
            .put(label, point.getValue());
      }
    }

    List<String> labels = new ArrayList<>(allLabelsSet);

    for (String seriesName : seriesNames) {
      List<Chart2DController.DataPoint2D> points = new ArrayList<>();
      Map<String, Double> dataByLabel = seriesDataByLabel.getOrDefault(seriesName, Map.of());

      for (int i = 0; i < labels.size(); i++) {
        String label = labels.get(i);
        double value = dataByLabel.getOrDefault(label, 0.0);
        points.add(new Chart2DController.DataPoint2D(i, value));
      }
      allDataPoints.add(points);
    }

    return new ChartData(seriesNames, allDataPoints, labels);
  }

  private String getSeriesDisplayName(String seriesName) {
    return seriesName == null ? "" : seriesName;
  }

  private record ChartData(
      List<String> seriesNames,
      List<List<Chart2DController.DataPoint2D>> dataPoints,
      List<String> labels) {}
}
