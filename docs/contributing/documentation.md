# Documentation

This project uses [MkDocs](https://www.mkdocs.org/) to generate the documentation. MkDocs is a static site generator that's geared towards building project documentation. Documentation source files are written in Markdown, and configured with a single YAML configuration file.

This project uses the [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/) theme.

## Installation

To assist in using mkdocs on any environment, mkdocs software can be installed through [Micromamba](https://mamba.readthedocs.io/en/latest/installation/micromamba-installation.html) python environment. To install mkdocs, run the following command:

Start the python environment by running the following command:

=== "macOS/Linux"
    ```shell
    micromamba activate cap-video
    ```

=== "Windows"
    ```powershell
    micromamba activate cap-video
    ```

## Serving the documentation locally

To serve the documentation locally, run the following command:

=== "macOS/Linux"
    ```shell
    mkdocs serve
    ```

=== "Windows"
    ```powershell
    mkdocs serve
    ```
