/* Copyright 2016 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License")
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package demo

import org.tensorflow.{Tensor, TensorFlow}

import java.io.PrintStream
import java.nio.file.Paths
import java.util.List

/** Sample use of the TensorFlow Java API to label images using a pre-trained model. */
object LabelImageInception {
  def printUsage(s: PrintStream) {
    val url: String =
      "https://storage.googleapis.com/download.tensorflow.org/models/inception5h.zip"
    s.println(
      "Scala program that uses a pre-trained Inception model (http://arxiv.org/abs/1512.00567)")
    s.println("to label JPEG images.")
    s.println("TensorFlow version: " + TensorFlow.version)
    s.println
    s.println("Usage: label_image <model dir> <image file>")
    s.println
    s.println("Where:")
    s.println("<model dir> is a directory containing the unzipped contents of the inception model")
    s.println("            (from " + url + ")")
    s.println("<image file> is the path to a JPEG image file")
  }

  def main(args: Array[String]) {
    if (args.length != 2) {
      printUsage(System.err)
      System.exit(1)
    }
    val modelDir: String = args(0)
    val imageFile: String = args(1)

    val graphDef: Array[Byte] = LabelImageUtils.readAllBytesOrExit(Paths.get(modelDir, "tensorflow_inception_graph.pb"))
    val labels: List[String] = LabelImageUtils.readAllLinesOrExit(Paths.get(modelDir, "imagenet_comp_graph_label_strings.txt"))
    val imageBytes: Array[Byte] = LabelImageUtils.readAllBytesOrExit(Paths.get(imageFile));

    var image: Tensor = LabelImageUtils.constructAndExecuteGraphToNormalizeInceptionImage(imageBytes)
    try {
      val labelProbabilities: Array[Float] = LabelImageUtils.executeInceptionGraph(graphDef, image)
      LabelImageUtils.printBestMatch(labelProbabilities, labels)
    } finally {
      image.close
    }
  }
}
